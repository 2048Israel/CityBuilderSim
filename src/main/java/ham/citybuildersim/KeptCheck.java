package ham.citybuildersim;

import ham.citybuildersim.ui.KeptBench;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each
 * month's redraw run over last month's nodes ends exactly where a page built new would, and costs
 * a fraction of it.
 *
 * WHY THIS EXISTS. A kept page (ui.Kept) runs the screen's own drawing code every month over the
 * nodes it made the month before. What can go wrong is what a node carries from one month into the
 * next: a value set under a condition that no longer holds, a list added to, a listener added
 * twice, a picture told only half its new figures. None of it shows on the month it is written;
 * all of it shows as a page that is not the page the city would draw. So this draws the page both
 * ways at every month of a played stretch and compares them - every node's words, style, the
 * values code set, handlers, tooltip and place, and at some months every pixel - and only then
 * times the kept month.
 *
 * It needs the JavaFX toolkit but no window: the page is laid out on a scene of its own
 * (ui.KeptBench). Where the toolkit cannot start (the cloud has no display) it says so in one line
 * and exits 0.
 *
 * What it has to prove:
 *   1. the page is kept: after its first draw a month's redraw hands back last month's nodes and
 *      keeps the same page in the column; the nodes made new in a month are few, and on most months
 *      none (counted over the stretch);
 *   2. it shows what a page built new shows: for MONTHS months of a played city the kept page's
 *      signature - every node's class, words, inline style, the values code set, handlers, tooltip
 *      and where layout put it - equals that of the page built new at the same month, line for line;
 *   3. and looks it: at three of those months the two pages are the same picture - no pixel more
 *      than SHADE_LEVELS apart in a channel, and at most SHADED_SHARE of them apart at all (the
 *      renderer shades a moved shape's edge a level or two apart; the count of months with the very
 *      same pixels is printed);
 *   4. what reshapes the page keeps it right: a details fold opened and closed, the month card's
 *      year view on and off - each redraw after the change equals a page built new;
 *   5. it is cheap: the kept page's month (its draw, then the CSS and layout after it) is within
 *      KEPT_MONTH_MS through Timing, and below the same month built new (both printed).
 *
 * Since UP2 the window's chrome too - the header and the tab rail, drawn by the real
 * refreshHeader() and refreshTabRail() on scenes of their own (the header at the window's width,
 * the rail at its height), the envelope drawn on its own as well:
 *   6. the chrome is kept: after its first draw a month's chrome draw keeps the header's and the
 *      rail's top nodes, hands back nine in ten of the nodes the first draw made, and makes few or
 *      none;
 *   7. it shows what a chrome built new shows: for MONTHS months the kept header's and rail's
 *      signature equals that of a chrome built new (every pool swapped for an empty one), line for
 *      line, the first difference printed with both lines, while the states change among the
 *      months - the clock running and stopped, a "stopped because" line shown and gone, the speed's
 *      rung, the screen on show (so the rail's lit tab, and a game menu with no clock), a notice
 *      raised, read and put away (the envelope's count, redrawn on its own);
 *   8. and looks it: at three of those months the header and the rail are the same pictures as the
 *      new ones (alike(): SHADE_LEVELS, SHADED_SHARE); one figure in the kept header made wrong by
 *      hand parts the signature and the picture, and the next kept draw sets it right; and what the
 *      rebuild did by being one is put back: a node that had the focus loses it, a button or tile
 *      drawn again under the pointer keeps its hover look and loses it when the pointer goes;
 *   9. a city opened forgets the chrome: the next draw makes every node new; and a draw of one pool
 *      inside a draw of another (the envelope inside the header) gives each its own nodes;
 *  10. it is cheap: the kept chrome's month (its draw, then the CSS and layout of both scenes) is
 *      within KEPT_CHROME_MS through Timing, the same month built new printed beside it.
 *
 * ...and the three panels (slice 2b), each on a scene of its own: the city panel, the construction
 * panel and the inbox's list, drawn by refreshCityPanel(), refreshConstructionPanel() and
 * refreshInboxList():
 *  11. the panels are kept: with the inbox open, the drawer open on every section and buildings on
 *      site, a month's draw keeps their top nodes, hands back nine in ten of the first draw's nodes
 *      and makes few (a row for a log entry or a notice that is new);
 *  12. they show what panels built new show, signature line for line, for MONTHS months, with the
 *      states changing: Dashboard with every section open, some, none, Summary, the chip's NEEDS YOU
 *      view, a section folded and unfolded, the drawer open and pinned, the inbox dropped down and
 *      put away, a notice raised, unfolded, read and taken away, buildings put on site and finishing,
 *      a building opened and one demolished; the same pictures at three months; one figure spoiled
 *      by hand is seen and set right; the city panel's scroller stays where it was put; a summary
 *      row keeps its hover look under a still pointer; an inbox button loses the focus;
 *  13. the whole chrome kept (header, rail and the three panels) in one month is within
 *      KEPT_ALL_CHROME_MS through Timing, the same month built new printed beside it.
 *
 * ...and City History (KP1), its page drawn kept from a pool of its own, the big chart and the two pins'
 * charts the screen's own and shared by the kept page and the one built new (the bench puts them back):
 *  14. City History is kept: each month's redraw keeps the page's top nodes and hands back nine in ten
 *      of the first draw's nodes, most months make none, and the big chart, both pins' charts, the
 *      legend's chips and the range buttons are the same nodes in the same boxes, in the scene;
 *  15. it shows what a page built new shows: for MONTHS months the kept page's signature equals that of the
 *      page built new (the pool, and the big chart's chips' pool, swapped for empty ones), line for line;
 *  16. and looks it: the same pictures at three of those months (alike()); one figure made wrong by hand parts
 *      signature and picture, and the next kept draw sets it right;
 *  17. what reshapes the page keeps it right: a line picked and unpicked, a preset, a picker group closed and
 *      opened, a word typed in the filter, the hard times' and the goods' folds, the log scale, real GDP alone
 *      in layers, a line hidden in the legend, the chart's window moved (a range, an episode's span, by hand),
 *      a line pinned and a pin taken off, the chart's full screen, the year book's card - the redraw after each
 *      and the one after that equal a page built new;
 *  18. the crosshair and the window survive a month: a window on the newest month follows it and one put by
 *      hand stays; the crosshair a MOUSE_MOVED set on the big chart's plot is there after a kept redraw and gone
 *      from a page built new (which takes the chart off the scene); a legend chip under the pointer is still the
 *      same chip, under it, with the same tooltip;
 *  19. the count-up only at 1x-10x: with the clock running at each rung of the speed ladder, a month that lands on
 *      People starts LIVING HERE's count-up, and on City History the left pin's, exactly at the rungs
 *      UserInterface.countsUp() allows (COUNT_UP_TOP) and always when the clock is paused; a count-up still
 *      running when a month lands past COUNT_UP_TOP is stopped;
 *  20. it is cheap: the kept City History month (its draw, then the pulse's CSS and layout) is within
 *      KEPT_HISTORY_MS through Timing, the same month built new printed beside it.
 *
 * ...and Infrastructure, Services and the land office (KP2), each page drawn kept from the screen's own pool and built
 * new from an empty one (the land office's map, which both hold, put back after), four sections each - the kept page, what
 * it shows, what reshapes it, what it costs - and the three that cut across them:
 *  21. Infrastructure is kept: on its Roads page, with transit built (a Bus Network ordered through the model if the city
 *      has none, so that the Transit page's fare card is there), each month's redraw keeps the top nodes, hands back nine
 *      in ten of the first draw's nodes, and on most months makes none;
 *  22. it shows what a page built new shows: for MONTHS months the signatures are equal line for line, the pictures the
 *      same at three of them, one figure made wrong by hand parts both and the next kept draw sets it right;
 *  23. what reshapes it keeps it right: every page, the Freight divisions, the roads', transit, freight and railway folds
 *      opened (each has to add to the page) and closed - the redraw after each and the one after a month lands equal a
 *      page built new, and the month's redraw hands back nine in ten of the page;
 *  24. it is cheap: the kept month within KEPT_INFRASTRUCTURE_MS through Timing, the month built new printed beside it;
 *  25 to 28. the same four for Services, on every sub-page of the four systems and every fold on them (the Health overview's
 *      sick rate to the safety books);
 *  29 to 32. the same four for the land office, on its sides and places, the best-N count, its fold, the way of paying, and
 *      a plot bought (the receipt, the offers that came onto the shelf: all from the model); the page says if the city
 *      owns ground on a second landmass, whose table is kept since KP4 (SectorScreen.oilTable: its rows are scopes), and how many sectors wait on ground;
 *  33. the land office's other pages: the city is brought to the end of its cash by buying through the model, so that the
 *      funding page exists; it and the page for when the city fell short, each an arrival, are drawn and a month later again,
 *      equal to pages built new;
 *  34. a control keeps the player's hand: on Transit's fare dial, through the screen's real show method, a slider pressed is
 *      held, and a month landing under it leaves the same node, its thumb, its reading (in the month's money, as a dial built
 *      at the thumb reads) and its ladder's shown value where the player put them; released, the fare is staged at the thumb
 *      and the page equals a page built new; after several kept months a value set outside any draw reaches the reading of
 *      the latest draw; the fare is unstaged at the end. And City History's filter box, with the focus on it (through
 *      Node.setFocused when this scene has no window to give it one - the line says which), keeps its node, text, caret and
 *      focus across a month through the real show method, and Kept.text sets a field that has none and leaves one that has;
 *  35. the hover is kept: on City History's row that moves the chart and chart card's head, on a door, a vitals cell that
 *      is a door, Services' door card and the land office's offer row and ore card, a pointer put over the node (a page may
 *      have none this month: the line says so) and the page drawn kept again leave the same node in the look its own
 *      mouse-entered gives it, and with the pointer off, the look its mouse-exited gives it.
 *
 * ...and Build and the construction page (KP3), each page drawn kept from the screen's own pool and built new from an
 * empty one, the same four sections as KP2's and the stopped pages, the controls and the hover:
 *  36 to 39. the Build screen: kept on the Overview and on a city category with two orders dialled up (so that its order bar
 *      shows); shown as a page built new shows it for MONTHS months, the pixels at three and the spoil proof; reshaped by
 *      every category in turn, every ring of the two city categories with the most of them, an order dialled and cleared
 *      with a card's own steppers on a city and on a market category, a card's stats pinned and unpinned, automatic
 *      building on and off (the Overview's cards; a category's head chip) - the redraw after each and the one after a
 *      month equal a page built new; the kept month within KEPT_BUILD_MS on both pages;
 *  40 to 43. the same four for the construction page, with the city's sites held on through the months (SITE_ROWS: a
 *      row each, a bar each on the timeline): its three tabs in turn, a building picked to demolish and a business's to buy
 *      out, two of it, a word typed in the find box and emptied; the kept month on Sites within KEPT_CONSTRUCTION_MS;
 *  44. the pages a build is stopped on (no deposit, coast, river, bridge, licence, land) and the run's funding and
 *      fell-short pages: each caused where the model can cause it (Game.buildRunStop() refuses the count; a run larger than
 *      the cash) and else drawn for a building that merely names the page - the line says which - drawn as an arrival and a
 *      month later again, equal to a page built new;
 *  45. the controls keep the player's hand: automatic building's dial pressed and held across a month (the same node,
 *      thumb, reading and Ladder.shown()), released and the page equal to one built new; the cap chips' yield box and the
 *      construction page's find box, with the focus, keep node, text, caret and focus across a month through the real
 *      show methods; a stepper pressed between draws refills the order bar in place and the next kept draw equals a page
 *      built new;
 *  46. the hover: a card's (i) hovered keeps its stat cover shown across a kept draw and hides it when the hover goes, and
 *      the Overview's tiles, a category's door and a small ring card keep the look their own mouse-entered gives them.
 *
 * ...and the Sectors tab (KP4), the list and a business's five pages in both views, each page drawn kept from the screen's pool and
 * built new from an empty one, the clock paused so that every month counts up (a month stepped by hand does) and the pages' animations
 * are finished where a signature is read:
 *  47. the list and a plain business's Operations page are kept: the top nodes stay, nine in ten handed back, most months make none;
 *  48. they show what a page built new shows, for MONTHS months with the pixels at three and the spoil proof - on the list, a plain
 *      business's Operations, its Income in both views and its Balance sheet as a statement;
 *  49. what reshapes them keeps them right: Show more and less; every sector of Sectors.KEYS on its Operations page; one business of
 *      each kind (a plain one, the shops, the landlords, a materials industry, Freight) on all five pages in both views; Freight's four
 *      divisions; the fold of every line, the goods fold, the order book and a statement's notes opened (each has to add to the page)
 *      and closed; the Statement view's change and common-size columns off and on - each redraw, and the one after a month lands,
 *      equal to a page built new;
 *  50. the pictures a page holds are kept: a materials industry's (IndustryPane), the legs' bars (LegBar) and, in a refinery's town
 *      (RefineryViewCheck's), the refinery's (RefineryPicture) stay the nodes they were from month to month, repaint only where the
 *      month's view moved, keep nine in ten of their parts, are not painted by a redraw in the same month, and equal a page built new;
 *  51. the count-up only at 1x-10x: with the clock at each rung of the speed ladder (and paused) a month that lands starts a count on
 *      the list's KEPT figure and its cards', a business's KEPT, the Income page's waterfall and the Operations page's ring exactly at
 *      the rungs UserInterface.countsUp() allows (COUNT_UP_TOP); past it nothing counts, the page is the page the month's figures
 *      draw, and a count still running from the month before is stopped;
 *  52. it is cheap: the kept list month within KEPT_SECTORS_MS and the kept month on the three business pages that weigh most within
 *      KEPT_SECTOR_MS through Timing, the same months built new printed beside them;
 *  53. the controls keep the player's hand: the oil reserve's FILL and RELEASE dials (in OilViewCheck's sea town), pressed and held
 *      across a month (the same node, its thumb, its reading and its ladder's shown value), released and equal to a page built new;
 *  54. the hover: a list card, a door and the crumb under a still pointer keep their look across a kept redraw.
 *
 * ...and the money screens (KP5), the Bank tab, the Finances tab and the city's fund, each page drawn kept from the screen's pool and built new from an
 * empty one, the clock paused so that every month counts up, a city that has paper of every kind on its books and a fund that holds:
 *  55. the Bank is kept: the Overview and the Profit page - the top nodes stay, nine in ten handed back, most months make none;
 *  56. it shows what a page built new shows, for MONTHS months with the pixels at three and the spoil proof: the Overview, Profit in both views, the
 *      Balance sheet as a statement;
 *  57. what reshapes it keeps it right: every page in both views, a statement fold and the lines inside it opened and closed, the waterfall's year, the
 *      statement's two columns - each redraw, and the one after a month lands, equal to a page built new; and a chip picked lands at the top of its page
 *      (the scroller of a kept page is put back to 0 by a pick of its own chip, left alone by a month);
 *  58. Finances is kept: the hub and a page of the position; 59. it shows what a page built new shows (the same as 56);
 *  60. what reshapes it: every area and page, the bond market's fold and the book of two bonds, Borrow's every kind and term and ask, callable, the highest
 *      yield, the typed ask and every money abroad, paper sold of every kind and its line's page and Close it now from each (the boxes typed in), and the
 *      foreign default and debt result menus, each an arrival and a month later;
 *  61 to 63. the same three for the city's fund: Portfolio kept (its months make the month's events, so no month may make more than a tenth of the page)
 *      and shown; its four pages, the rows that fold, a security opened with the ticket's every choice, Search's words and chips, Activity's groups and months;
 *  64. the pictures are kept: CurveChart, DeskCurve, DeskBars, PathChart, BuybackLadder, AuctionChart, Columns, RankBar and the TimeCharts stay the nodes they
 *      were, draw again only where the month's figures moved, draw nothing on a second redraw in the same month (a TimeChart: no more than the two frames it is
 *      told), and equal a page built new;
 *  65. the count-up only at 1x-10x: at each rung of the speed ladder and paused a month that lands pops Bank's PROFIT figure, grows its capital gauge and
 *      counts the fund's worth and pops a payment, exactly at the rungs countsUp() allows; past it nothing animates and a running animation is stopped;
 *  66. the boxes keep the player's hand: Close it now's two, the ask's, the yield boxes of Borrow's and the rollover's chips and the fund's search box, with the
 *      focus and typed into, a month landed through the real show method - the same node, text, caret and focus - and a chip pressed under the focus; and
 *      (KP2b) Enter fired in each yield box with "4.5" typed: the same node, the text a page built new shows ("4.500"), the caret at the end, the focus kept;
 *  67. the hover: a Bank door card, a rung's name on the ladder, a Finances card and a row of the book, a fund holding's row;
 *  68. it is cheap: the kept month on the heaviest page of each within KEPT_BANK_MS, KEPT_FINANCES_MS and KEPT_FUND_MS through Timing, built new beside it.
 *
 * ...and the Government tab, Trade & the World with its partners page and Policy with zoning's pages (KP6), each page drawn kept from the
 * screen's pool and built new from an empty one, the clock paused so that every month counts up:
 *  69. Government is kept: each of its four pages, the top nodes stay, nine in ten handed back, most months make none (the Overview's lists show a line in
 *      the months it took in or paid out something, so there no month may make more than a tenth of the page);
 *  70. it shows what a page built new shows, for MONTHS months with the pixels at three and the spoil proof, on every page;
 *  71. what reshapes it keeps it right: every page, every fold (the treasury's month, the paper, the output's months, the bridge's two columns' "N more")
 *      and every line that opens into who pays, each opened and closed, and a door's target - each redraw, and the one after a month lands, equal to a page
 *      built new - in the city as it is, owing paper of every kind (the debt fold and the paper's lines have to add), and in a deficit made by hand with the
 *      central bank printing for the treasury and arrears owed (the cards the Overview draws only then; the dials put back after);
 *  72 to 74. the same three for Trade & the World, on its six pages and a nation's page behind Partners: every page and sub-page, every fold and line (the
 *      month's ledger and the eight moneys over the years have to add), the river and the steps, what we trade by good and by business, the table of the month
 *      and of the year, a good's board, three nations' pages and back to the table, the exchange's ask and buying and selling, each of the eight moneys, the
 *      vault's mix, and a door's target; the eight over the years' two charts are as wide as the card they sit in, on the kept draw and the month after;
 *  75 to 77. the same three for Policy, on the hub, Taxes' Profit page, Money's policy rate, Promises' Schools and Food, zoning's ground and a zone's page:
 *      every fixed page of the five areas and a zone's page for every sector, zoned or not, the forest law, every fold and line (a tax's table, a payer's
 *      own move, the zero payers, the floor's table, the rate's history, a course's own price, the food rows), a change staged on every page that takes
 *      one and dropped (the tray, and the dial cards drawn at the thumb), and the currency page and the reform page under every regime; and People's WHO
 *      GOES SHORT card, which Policy's Food page shares, with people short of food;
 *  78. the controls on Policy keep the player's hand: on a tax page, the policy rate and a zone's ground dial, and (KP2b) the policy rate page's target
 *      ladder and HOW STRICT (which apply at once: the model put back) and a zone's ITS TAXES second dial, through the real show method, a slider
 *      pressed and dragged two steps and a month landed is the same node, still held, the thumb where the hand left it, the reading and the dial card's
 *      effects at the thumb (HOW STRICT's line under it the held step's words, not the city's); a draw with nothing changed sets no reading's text or
 *      style; released, the change is staged at the thumb, the tray shows it and the page equals a page built new; dropped, equal again;
 *      and a dial card that comes in front of a held dial (a course's own price on the Schools page, a payer's own move on Profit) leaves the held slider
 *      the same node, at its thumb, with no dial in front of it carrying the thumb;
 *  79. the count-up only at 1x-10x: at each rung of the speed ladder and paused a month that lands counts Trade's SOLD ABROAD and BOUGHT ABROAD up from last
 *      month's and grows the bars of the Overview and of What we trade exactly at the rungs countsUp() allows; past it nothing animates, each figure reads
 *      this month's words and the page is the page built new, and a count still running is stopped; the Government and Policy pages start no animation at
 *      any rung (they have none);
 *  80. the hover: on Government a door, a status cell, a key row of the split ring, a bridge row and a ranked bar's row; on Trade a door card, a mirror
 *      row's ground and a status cell; on Policy an area card and the tall rows of a payer, a course and a zone - each keeps the look its own mouse-entered
 *      gives it under a still pointer across a kept redraw, and loses it with the pointer; and the tooltips are kept: a node a month's redraw keeps keeps
 *      its own Tooltip (Kept.tip) on every page of the three screens;
 *  81. it is cheap: the kept month on Government's Overview within KEPT_GOVERNMENT_MS, on Trade's Overview and its heaviest page within KEPT_TRADE_MS and
 *      on Policy's hub and its dial-heavy pages within KEPT_POLICY_MS through Timing, built new beside it.
 *
 * ...and Household money and the Rankings tab (KP7), the last two screens on the rail, so that no screen on it is rebuilt by the clock; each page drawn kept
 * from the screen's pool and built new from an empty one, the clock paused so that every month counts up:
 *  82. Household money is kept: the top nodes stay, nine in ten of the nodes handed back, most months make none;
 *  83. it shows what a page built new shows, for MONTHS months with the pixels at three and the spoil proof;
 *  84. what reshapes it keeps it right: each view, every cell of the grid opened (a family cell of each shape and tier with anybody in it, and every row below
 *      the rule), none, the city's month as a statement opened and closed, the books stacked under the grid (the page 1000 wide) and beside it again, and the
 *      page shown through the real show method - each redraw, and the one after a month lands (a cell stays open across it), equal to a page built new;
 *  85 to 87. the same three for the Rankings tab, which paints its table on canvases that no signature holds, so every choice's redraw, and the one after a
 *      month, is also the same picture as a page built new: each board (Your world where the city has nations), money, prices and conversion, every fifth
 *      measure, each filter and Around you, Browse all, a measure pinned, a place's drawer; then the city saved into slot 10 of the harness's own folder, a
 *      read of the saved cities waited for, and Your cities at Now and at Your age, their prices and moneys and drawers;
 *  88. the clock never rebuilds a rail screen: a page drawn the old way twice under one name moves UserInterface.rebuiltInPlace by one, and an arrival by none
 *      (the condition caused); then every tab of the rail, Household money, the construction page and the city's fund on Finances, each shown through its real
 *      show method and then given three months with the screen's own redraw after each (what the clock does), moves the count by none, keeps the page's top
 *      nodes the same nodes, keeps the same screen on show, and hands back nodes of the page it redraws over;
 *  89. the hover and the tooltips: on Household money a vitals cell that is a door; on the Rankings tab a measure line, which keeps its hover look under a
 *      still pointer across a redraw and loses it with the pointer, and the table's picture, which lights the row under a pointer that has not moved and none
 *      once it has gone; and a node a month's redraw keeps keeps its own Tooltip (Kept.tip) on both pages, with a cell open, a drawer open and every measure open;
 *  90. it is cheap: the kept month on Household money (as it opens and with a cell's books open) within KEPT_HOUSEHOLD_MS and on the Rankings tab (as it opens
 *      and with every measure unfolded) within KEPT_RANKINGS_MS through Timing, built new beside it;
 *  91. (KP2b) a slider greyed while held lets go: the policy rate's dial pressed (and, the second time, dragged a step), then greyed, is not held; ungreyed,
 *      a draw with another value staged under it puts the thumb on that value and the page equals a page built new, and dropped the thumb is back;
 *      and a door card's style is set once a draw: on the Policy hub and the Taxes page, a draw with nothing changed hears no change of it;
 *  92. a redraw with nothing new sets nothing (KP2b): a second kept draw of a page in the same month writes no style, text, graphic, padding or width on a
 *      node it hands back - the condition caused (a style set to what it is moves no listener, and set to another and back moves it twice), a berth kind
 *      gone to none drawn as a row built new (a hand-made berth: no city has the page), then the Sectors list and a business's five pages, the statements
 *      with a note open, the Bank's seven pages, Finances' hub and every page of its six areas (a line's page, Close it now, the window abroad shut), the
 *      fund's security, Trade's six and a nation's, Policy's hub and pages with Zoning's, Infrastructure's four, Services' sub-pages, the land office,
 *      Household money (each view, every cell of the grid opened, the statement, the books stacked) and the Rankings tab (each board, money, prices and
 *      conversion, some measures (every tenth), each filter, Around you, Browse all, a pin, a place's drawer, Your cities), each with where the writes that are left come
 *      from; the writes of the screens outside the sweep (NOT_KP2B) are named and not counted;
 *  93. the Rankings table's picture (KP2b): a kept draw of the page writes its cursor none (the hand is in its style, which Kept leaves alone), and the band
 *      that lights the row under the pointer follows a pointer a wheel leaves still - a ScrollEvent through the table's canvas moves the page, no
 *      mouse-moved follows, and after it, and after a month's draw, the band is on the row the pointer is over now and not on the one it was over before.
 * ...and the research screens on the Sectors and Build tabs (R5, slice B2), on ResearchViewCheck.researchTown(), a city founded the 1900 way (a played
 * city has nothing of either to draw, and says so):
 *  96. a sector's Investors page with its equipment, kept: the sector of the fixture's unfitted stack has ITS EQUIPMENT with the piece's card, the
 *      stack's row, how many carry it and the model's verdict; the page kept over last month's nodes is the page built new for R5_MONTHS months and hands
 *      back nine in ten of its nodes; the farms' page has none until the tractor is found here, then a card with its origin, what it does and "its gain
 *      cannot be had yet", and the find lost and found again reshapes it as a page built new is reshaped; a played city has no ITS EQUIPMENT;
 *  97. a NEW build card, kept: the category page holding the fixture's new furnace shows the chip and the violet edge, where it came from, the strip
 *      under the head and the dot on the category's chip; the seen rule from both sides (NEW the month the card is first drawn, a redraw in that month
 *      too, and not the month after while the model alone still says NEW; the dot on another page follows it; a reload shows NEW again; the rule alone
 *      on a seen map and two months); a lab's card says what a lab is for, in either kind of city; a played city has no NEW anywhere.
 */
public class KeptCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        checks++;
        if (!ok) fails++;
        System.out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The months the city is played to before the page is watched: a city with a history, households of every shape, a full ladder. */
    static final int CITY_MONTHS = 600;

    /** The months watched, kept against new: two years and a month, so a January and a year's turn are among them. */
    static final int MONTHS = 25;

    /** The budget for the kept People page's month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC. */
    static final double KEPT_MONTH_MS = 12;

    /** Where a month's log goes while month() runs it: nowhere. */
    private static final PrintStream QUIET = new PrintStream(OutputStream.nullOutputStream());

    static <T> T fx(Callable<T> c) throws Exception {
        FutureTask<T> t = new FutureTask<>(c);
        Platform.runLater(t);
        return t.get(10, TimeUnit.MINUTES);
    }

    /** The toolkit started, or why not (then this harness has nothing to run on). */
    static String startJavaFx() {
        try {
            if (java.awt.GraphicsEnvironment.isHeadless()) return "this JVM is headless (no display)";
            Platform.setImplicitExit(false);
            CountDownLatch up = new CountDownLatch(1);
            Platform.startup(up::countDown);
            if (!up.await(60, TimeUnit.SECONDS)) return "the toolkit did not come up in 60 s";
            return null;
        } catch (Throwable t) {
            String why = t.getMessage() == null ? "" : ": " + t.getMessage().replaceAll("\\s+", " ").trim();
            return t.getClass().getSimpleName() + why;
        }
    }

    /** What is done after every month played, with the log off, while a fixture has to be held through the months (KP3: the city's sites); null for nothing. */
    static Runnable afterMonth;

    /** A month, as the clock runs it, its log kept off this harness's output. */
    static void month(Game g) {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            g.toggleNextMonth();
            if (afterMonth != null) afterMonth.run();
        } finally {
            System.setOut(real);
        }
    }

    /** A call made with the game's narration kept off this harness's output (a purchase tells the log what it bought). */
    static <T> T quietly(Callable<T> call) throws Exception {
        PrintStream real = System.out;
        System.setOut(QUIET);
        try {
            return call.call();
        } finally {
            System.setOut(real);
        }
    }

    /** The first line where two signatures part, or null when they are the same. */
    static String firstDifference(List<String> kept, List<String> fresh) {
        int n = Math.min(kept.size(), fresh.size());
        for (int i = 0; i < n; i++) {
            if (!kept.get(i).equals(fresh.get(i))) {
                // Shown from where the two lines part, so a long line's difference is not cut off.
                int k = 0;
                String a = kept.get(i), b = fresh.get(i);
                while (k < a.length() && k < b.length() && a.charAt(k) == b.charAt(k)) k++;
                int from = Math.max(0, k - 60);
                String head = from > 0 ? "..." : "";
                // Where the node is: the start of the line (its depth, class, words and style), which a long line's tail does not show.
                String start = from > 0 ? "\n      node:  " + a.substring(0, Math.min(from, 110)) + (from > 110 ? "..." : "") : "";
                // ...and how many more lines part (a wrapped label's height parts one line, a node made or lost parts the rest), the next of them in full.
                int more = 0, next = -1;
                for (int j = i + 1; j < n; j++) {
                    if (kept.get(j).equals(fresh.get(j))) continue;
                    more++;
                    if (next < 0) next = j;
                }
                String rest = more == 0 ? "" : "\n      " + more + " more line(s) part, the next " + next + ":\n      kept:  " + clip(kept.get(next).strip())
                        + "\n      fresh: " + clip(fresh.get(next).strip());
                return "line " + i + " (from character " + from + ")" + start + "\n      kept:  " + head + clip(a.substring(from))
                        + "\n      fresh: " + head + clip(b.substring(from)) + rest;
            }
        }
        if (kept.size() != fresh.size()) return "kept has " + kept.size() + " lines, fresh " + fresh.size();
        return null;
    }

    /** A snapshot (width, height, then ARGB) written as a PNG, for -Dkeptcheck.out=DIR: the two pages a pixel check found apart. */
    static void png(int[] px, java.nio.file.Path to) throws java.io.IOException {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(px[0], px[1], java.awt.image.BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, px[0], px[1], px, 2, px[0]);
        java.nio.file.Files.createDirectories(to.getParent());
        javax.imageio.ImageIO.write(img, "png", to.toFile());
    }

    /**
     * The most a pixel of the kept page may differ from the new page's, in any channel, out of 255:
     * the edge of a small shape a kept picture moved to another place on the page is shaded by the
     * renderer's cached outline a level or two apart from a shape drawn there new (People's
     * household tiles, reused by their shape wherever the sort puts them, measured up to 30 and 58
     * on single edge pixels of the half-filled dots of a moved tile at months 617 and 630). A wrong
     * figure, colour or bar is far past it in level or in count.
     */
    static final int SHADE_LEVELS = 64;

    /** ...and at most this share of the page's pixels may differ at all (114 and 110 of 2.8 million measured). */
    static final double SHADED_SHARE = 0.001;

    /** Whether two snapshots are the same picture: the same size, no pixel past SHADE_LEVELS, few past none. */
    static boolean alike(int[] a, int[] b) {
        if (a[0] != b[0] || a[1] != b[1]) return false;
        int n = 0;
        for (int i = 2; i < a.length; i++) {
            if (a[i] == b[i]) continue;
            n++;
            for (int s = 0; s < 32; s += 8) if (Math.abs(((a[i] >> s) & 255) - ((b[i] >> s) & 255)) > SHADE_LEVELS) return false;
        }
        return n <= SHADED_SHARE * (a.length - 2);
    }

    /** Where two snapshots part: their sizes, or how many pixels differ and the box round them. */
    static String pixelDifference(int[] a, int[] b) {
        if (a[0] != b[0] || a[1] != b[1]) return a[0] + "x" + a[1] + " against " + b[0] + "x" + b[1];
        int w = a[0], h = a[1], n = 0, x0 = w, y0 = h, x1 = -1, y1 = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (a[2 + y * w + x] != b[2 + y * w + x]) {
                    n++;
                    x0 = Math.min(x0, x);
                    y0 = Math.min(y0, y);
                    x1 = Math.max(x1, x);
                    y1 = Math.max(y1, y);
                }
            }
        }
        return n + " pixels, in " + x0 + "," + y0 + " to " + x1 + "," + y1;
    }

    static String clip(String s) {
        return s.length() > 300 ? s.substring(0, 300) + "..." : s;
    }

    static double median(List<Double> v) {
        List<Double> s = new ArrayList<>(v);
        Collections.sort(s);
        return s.isEmpty() ? Double.NaN : s.get(s.size() / 2);
    }

    /** The budget for the kept chrome's month - the header's and the rail's draw, then the pulse's CSS and layout over both - at CITY_MONTHS, on the PC. */
    static final double KEPT_CHROME_MS = 4;

    /** The screens the chrome is shown on, one a month: the tab lit on the rail follows, and the last is a game menu (no clock, no rail). */
    static final String[] CHROME_SCREENS = {"showPopulationInfoMenu", "showLandMenu", "showSectorMenu", "showBankMenu",
            "showHistoryMenu", "showPolicyMenu", "showMainMenu"};

    /**
     * Sections 6 to 10: the header and the tab rail kept (UP2). The same city, played on; the bench
     * draws the real refreshHeader() and refreshTabRail() on scenes of their own, kept and with every
     * node new, beside the page.
     */
    static void chrome(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 6: the chrome is kept ---");
        fx(() -> {
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(CHROME_SCREENS[0]);
            return null;
        });
        int[] first = fx(() -> {
            bench.drawChromeKept();
            bench.chromePasses();
            return bench.chromeCounts();
        });
        int firstMade = first[0] + first[2] + first[4];
        System.out.printf("  the first draw made %d nodes through Kept (the header's %d, the envelope's %d, the rail's %d)%n",
                firstMade, first[0], first[2], first[4]);
        int sameTops = 0, quiet = 0, madeMost = 0;
        long back = 0;
        for (int m = 1; m <= MONTHS; m++) {
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.chromeTops();
                month(g);
                bench.drawChromeKept();
                bench.chromePasses();
                int[] c = bench.chromeCounts();
                return new Object[]{before.equals(bench.chromeTops()), c[0] + c[2] + c[4], c[1] + c[3] + c[5]};
            });
            if ((boolean) r[0]) sameTops++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            back += (int) r[2];
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, back / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the nodes the header and the rail drew (the same top nodes, " + sameTops + " of " + MONTHS + ")",
                sameTops == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + back / MONTHS + " of " + firstMade + ")",
                back / MONTHS * 10 >= firstMade * 9L);
        check("most months make no node at all (" + quiet + " of " + MONTHS + "), and none makes more than a tenth (" + madeMost + ")",
                quiet * 2 > MONTHS && madeMost * 10 <= firstMade);

        System.out.println("\n--- 7 and 8: the kept chrome against a chrome built new, in every state ---");
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        int differ = 0, shotsSame = 0, shotsAlike = 0, envelopeDiffer = 0;
        String firstDiffer = null;
        Notice a = new Notice("keptcheck-a", "Kept check A", List.of("a"), g.getMonth());
        Notice b = new Notice("keptcheck-b", "Kept check B", List.of("b"), g.getMonth());
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                // The states change among the months: the clock running and stopped (stopped itself, or not), the speed's rung,
                // the screen on show (so the tab lit), and a notice raised, read and put away.
                boolean running = at % 4 == 0 || at % 4 == 2;
                bench.clock(running, at % 4 == 1 ? "Kept check: a notice stopped the clock" : null);
                bench.speed(at % 9);
                bench.screen(CHROME_SCREENS[at % CHROME_SCREENS.length]);
                month(g);
                bench.drawChromeKept();
                // ...and the inbox changes after the draw, as a notice read elsewhere does: the envelope is drawn on its own.
                switch (at % 6) {
                    case 1 -> g.getInbox().all().add(a);
                    case 2 -> g.getInbox().all().add(b);
                    case 3 -> g.getInbox().markRead(a, g.getMonth());
                    case 4 -> g.getInbox().markRead(b, g.getMonth());
                    case 5 -> {
                        g.getInbox().all().remove(a);
                        g.getInbox().all().remove(b);
                    }
                    default -> { }
                }
                bench.drawEnvelope();
                bench.chromePasses();
                List<String> kept = bench.chromeSignature();
                int[][] keptPx = shotAt.contains(at) ? bench.chromePixels() : null;
                bench.showFreshChrome();
                List<String> fresh = bench.chromeSignature();
                int[][] freshPx = shotAt.contains(at) ? bench.chromePixels() : null;
                bench.restoreKeptChrome();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + " (" + at + "), " + r[0];
            }
            if (r[1] != null) {
                int[][] k = (int[][]) r[1], f = (int[][]) r[2];
                for (int i = 0; i < 2; i++) {
                    if (java.util.Arrays.equals(k[i], f[i])) shotsSame++;
                    if (alike(k[i], f[i])) shotsAlike++;
                    else System.out.println("  " + (i == 0 ? "header" : "rail") + " pixels differ at month " + g.getMonth() + ": " + pixelDifference(k[i], f[i]));
                    if (!java.util.Arrays.equals(k[i], f[i]) && System.getProperty("keptcheck.out") != null) {
                        String to = System.getProperty("keptcheck.out");
                        png(k[i], java.nio.file.Path.of(to, "chrome-kept-" + i + "-m" + g.getMonth() + ".png"));
                        png(f[i], java.nio.file.Path.of(to, "chrome-new-" + i + "-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        g.getInbox().all().remove(a);
        g.getInbox().all().remove(b);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept chrome's signature equals the new chrome's at every month, through every state (" + differ + " differ)", differ == 0);

        System.out.println("\n--- 8: and its pixels ---");
        System.out.printf("  %d of %d pictures the very same pixels%n", shotsSame, shotAt.size() * 2);
        check("the kept header and rail are the same pictures as the new ones at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + " of " + shotAt.size() * 2 + ")", shotsAlike == shotAt.size() * 2);

        Object[] spoilt = fx(() -> {
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.chromePasses();
            String was = bench.spoilHeader();
            bench.chromePasses();
            List<String> kept = bench.chromeSignature();
            int[][] keptPx = bench.chromePixels();
            bench.showFreshChrome();
            List<String> fresh = bench.chromeSignature();
            int[][] freshPx = bench.chromePixels();
            bench.restoreKeptChrome();
            bench.drawChromeKept();
            bench.chromePasses();
            List<String> mended = bench.chromeSignature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx[0], freshPx[0]), firstDifference(mended, fresh)};
        });
        check("one figure made wrong in the kept header (\"" + spoilt[0] + "\") parts its signature from the new chrome's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        // What the rebuild did by being a rebuild, put back by hand: the focus, and the look under a still pointer.
        Object[] hand = fx(() -> {
            boolean had = bench.focusPlay();
            bench.drawChromeKept();
            boolean kept = bench.headerHasFocus();
            return new Object[]{had, kept};
        });
        if (!(boolean) hand[0]) System.out.println("  the scene would not give the play button the focus here: the focus check is skipped");
        else check("a header node that had the focus loses it at the next kept draw, as the rebuild took it", !(boolean) hand[1]);

        String[] looks = fx(() -> {
            bench.screen(CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.chromePasses();
            // The rail's first button (Build; People is the lit tab) and the first tile, each with the pointer put over it.
            javafx.scene.Node button = bench.railChild(0);
            javafx.scene.Node tile = ((javafx.scene.Parent) bench.headerChild(2)).getChildrenUnmodifiable().get(0);
            String buttonRest = button.getStyle(), tileRest = tile.getStyle();
            button.getOnMouseEntered().handle(null);
            tile.getOnMouseEntered().handle(null);
            String buttonHover = button.getStyle(), tileHover = tile.getStyle();
            button.getOnMouseExited().handle(null);
            tile.getOnMouseExited().handle(null);
            KeptBench.hover(button, true);
            KeptBench.hover(tile, true);
            bench.drawChromeKept();
            String buttonUnder = bench.railChild(0).getStyle(), tileUnder = ((javafx.scene.Parent) bench.headerChild(2)).getChildrenUnmodifiable().get(0).getStyle();
            KeptBench.hover(button, false);
            KeptBench.hover(tile, false);
            bench.drawChromeKept();
            String buttonAway = bench.railChild(0).getStyle(), tileAway = ((javafx.scene.Parent) bench.headerChild(2)).getChildrenUnmodifiable().get(0).getStyle();
            return new String[]{buttonRest, buttonHover, buttonUnder, buttonAway, tileRest, tileHover, tileUnder, tileAway};
        });
        check("a rail button drawn again under the pointer keeps its hover look, and loses it when the pointer goes",
                !looks[0].equals(looks[1]) && looks[2].equals(looks[1]) && looks[3].equals(looks[0]));
        check("...and so does a header tile", !looks[4].equals(looks[5]) && looks[6].equals(looks[5]) && looks[7].equals(looks[4]));

        System.out.println("\n--- 9: a city opened forgets the chrome ---");
        int[] again = fx(() -> {
            bench.clock(true, null);
            bench.speed(3);
            bench.drawChromeKept();
            int[] quietCounts = bench.chromeCounts();
            bench.forgetChrome();
            bench.drawChromeKept();
            int[] afterCounts = bench.chromeCounts();
            return new int[]{quietCounts[0] + quietCounts[2] + quietCounts[4], afterCounts[0] + afterCounts[2] + afterCounts[4],
                    afterCounts[1] + afterCounts[3] + afterCounts[5]};
        });
        System.out.printf("  a draw before: %d nodes made; the draw after the forgetting: %d made, %d handed back%n", again[0], again[1], again[2]);
        check("before the forgetting a draw makes few nodes, after it every node is new (" + again[1] + " made, " + again[2] + " handed back)",
                again[0] * 10 <= firstMade && again[2] == 0 && again[1] * 10 >= firstMade * 9L);

        boolean nested = fx(KeptBench::nestedDrawsKeepEach);
        check("a draw within another pool's draw gives each pool its own nodes (the envelope inside the header)", nested);

        System.out.println("\n--- 10: what the kept chrome costs ---");
        double[] cost = fx(() -> {
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawChromeKept();
                double[] p = bench.chromePasses();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: every node made again in the header and the rail, as the redraw was before UP2.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFreshChrome();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptChrome();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the chrome's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_CHROME_MS);
        String timed = "the kept chrome's month is within KEPT_CHROME_MS (" + KEPT_CHROME_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;
    }

    /** The budget for the whole chrome kept, in one month: the header, the rail and the three panels, drawn and laid out. */
    static final double KEPT_ALL_CHROME_MS = 8;

    /** The panels' states, month by month: the city panel's mode, and which of its sections are open. */
    static void panelStates(KeptBench bench, int at) {
        switch (at % 5) {
            case 0 -> { bench.cityMode(true, false); bench.sectionsAll(true); }
            case 1 -> bench.cityMode(false, false);
            case 2 -> { bench.cityMode(true, false); bench.sectionsAll(false); bench.section("econ", true); bench.section("labour", true); }
            case 3 -> { bench.cityMode(true, false); bench.sectionsAll(false); }
            default -> bench.cityMode(true, true);      // the header's chip opened the drawer on NEEDS YOU: Summary, whatever is stored
        }
        bench.section("land", at % 2 == 0);              // one section folded and unfolded among the months
        bench.drawer(at % 3 != 0, at % 4 == 1);
    }

    /**
     * Sections 11 to 13: the city panel, the construction panel and the inbox's list kept (UP2, slice
     * 2b), each on a scene of its own, beside the header and the rail of 6 to 10.
     */
    static void panels(Game g, KeptBench bench) throws Exception {
        Notice[] notice = new Notice[2];
        // Buildings to put on site, by the model's own call: three templates that take construction points.
        List<BuildingsTemplate> picks = new ArrayList<>();
        BuildingManager buildings = g.getBuildingManager();
        for (int i = 0; i <= buildings.getMaxTemplateId() && picks.size() < 3; i++) {
            BuildingsTemplate t = buildings.getTemplate(i);
            if (t != null && t.getConstructionPoints() > 0) picks.add(t);
        }
        // The model's own calls cause what the played city may not have: a building opened and one demolished in the stretch.
        System.out.println("\n--- 11: the panels are kept ---");
        int sitesNow = g.getBuildingManager().getUnderConstruction();
        Notice panelsNotice = new Notice("keptcheck-p", "Kept check panels", List.of("a line", "another line"), g.getMonth());
        fx(() -> {
            g.getBuildLog().record("Kept Works", 2, g.getMonth());
            g.getDemolitionLog().record("Kept Mill", 3, "Industry", g.getMonth(), 12_000);
            buildings.addStack(picks.get(0), 20_000, false);
            buildings.addStack(picks.get(1), 20_000, false);
            g.getInbox().all().add(panelsNotice);
            bench.cityMode(true, false);
            bench.sectionsAll(true);
            bench.drawer(true, false);
            bench.inboxList(true, "keptcheck-p");
            return null;
        });
        int[] first = fx(() -> {
            bench.drawPanelsKept();
            bench.panelPasses();
            return bench.panelCounts();
        });
        int firstMade = first[0] + first[2] + first[4];
        System.out.printf("  the first draw made %d nodes through Kept (the city panel's %d, the construction panel's %d, the inbox list's %d);"
                + " %d building(s) under construction%n", firstMade, first[0], first[2], first[4], sitesNow);
        int sameTops = 0, quiet = 0, madeMost = 0, withSites = 0;
        long back = 0, madeAll = 0;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.panelTops();
                // The logs keep an entry two years: one is put in again now and then, so the stretch has them throughout.
                if (at % 12 == 0) {
                    g.getBuildLog().record("Kept Works", 2, g.getMonth());
                    g.getDemolitionLog().record("Kept Mill", 3, "Industry", g.getMonth(), 12_000);
                }
                month(g);
                bench.drawPanelsKept();
                bench.panelPasses();
                int[] c = bench.panelCounts();
                return new Object[]{before.equals(bench.panelTops()), c[0] + c[2] + c[4], c[1] + c[3] + c[5],
                        g.getBuildingManager().getUnderConstruction() > 0};
            });
            if ((boolean) r[0]) sameTops++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            madeAll += (int) r[1];
            back += (int) r[2];
            if ((boolean) r[3]) withSites++;
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d,"
                + " %d in all; months with a building under construction: %d%n", MONTHS, back / MONTHS, quiet, madeMost, madeAll, withSites);
        check("each month's redraw keeps the nodes the three panels drew (the same top nodes, " + sameTops + " of " + MONTHS + ")",
                sameTops == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + back / MONTHS + " of " + firstMade + ")",
                back / MONTHS * 10 >= firstMade * 9L);
        // A log gains a row when a building opens or is demolished, and the inbox a row when a notice is raised: the rows
        // that come are made, the rest are handed back. "Few" is a fiftieth of the first draw a month, and no month a tenth.
        check("the nodes made in a month are few (" + madeAll + " in all over " + MONTHS + " months, at most " + madeMost + " in one)",
                madeAll * 50 <= (long) firstMade * MONTHS && madeMost * 10 <= firstMade);

        System.out.println("\n--- 12: the kept panels against panels built new, in every state ---");
        List<Integer> shotAt = List.of(2, 9, MONTHS);
        int differ = 0, shotsSame = 0, shotsAlike = 0;
        String firstDiffer = null;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                panelStates(bench, at);
                // The inbox: dropped down and put away, a notice raised, unfolded, read and taken away.
                switch (at % 6) {
                    case 1 -> notice[0] = add(g, "keptcheck-a", "Kept check A");
                    case 2 -> notice[1] = add(g, "keptcheck-b", "Kept check B");
                    case 3 -> g.getInbox().markRead(notice[0], g.getMonth());
                    case 4 -> g.getInbox().markRead(notice[1], g.getMonth());
                    case 5 -> {
                        g.getInbox().all().remove(notice[0]);
                        g.getInbox().all().remove(notice[1]);
                    }
                    default -> { }
                }
                bench.inboxList(at % 3 != 1, at % 6 == 2 || at % 6 == 3 ? "keptcheck-a" : at % 6 == 4 ? "keptcheck-b" : "");
                // A building put on site in the stretch, and the logs: a demolition and a building opened now and then.
                if (at == 8) buildings.addStack(picks.get(2), 20_000, false);
                if (at % 7 == 3) g.getDemolitionLog().record("Kept Mill " + at, 1 + at % 3, at % 2 == 0 ? "City order" : "Industry", g.getMonth(), at % 2 == 0 ? 0 : 7_500);
                if (at % 5 == 2) g.getBuildLog().record("Kept Works " + at, 1 + at % 4, g.getMonth());
                month(g);
                bench.drawPanelsKept();
                bench.panelPasses();
                List<String> kept = bench.panelSignature();
                int[][] keptPx = shotAt.contains(at) ? bench.panelPixels() : null;
                bench.showFreshPanels();
                List<String> fresh = bench.panelSignature();
                int[][] freshPx = shotAt.contains(at) ? bench.panelPixels() : null;
                bench.restoreKeptPanels();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + " (" + at + "), " + r[0];
            }
            if (r[1] != null) {
                int[][] k = (int[][]) r[1], f = (int[][]) r[2];
                for (int i = 0; i < 3; i++) {
                    if (java.util.Arrays.equals(k[i], f[i])) shotsSame++;
                    if (alike(k[i], f[i])) shotsAlike++;
                    else System.out.println("  panel " + i + " pixels differ at month " + g.getMonth() + ": " + pixelDifference(k[i], f[i]));
                    if (!java.util.Arrays.equals(k[i], f[i]) && System.getProperty("keptcheck.out") != null) {
                        String to = System.getProperty("keptcheck.out");
                        png(k[i], java.nio.file.Path.of(to, "panel-kept-" + i + "-m" + g.getMonth() + ".png"));
                        png(f[i], java.nio.file.Path.of(to, "panel-new-" + i + "-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        g.getInbox().all().remove(notice[0]);
        g.getInbox().all().remove(notice[1]);
        g.getInbox().all().remove(panelsNotice);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept panels' signatures equal the new panels' at every month, through every state (" + differ + " differ)", differ == 0);
        System.out.printf("  %d of %d pictures the very same pixels%n", shotsSame, shotAt.size() * 3);
        check("the kept panels are the same pictures as the new ones at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + " of " + shotAt.size() * 3 + ")", shotsAlike == shotAt.size() * 3);

        Object[] spoilt = fx(() -> {
            bench.cityMode(true, false);
            bench.sectionsAll(true);
            bench.inboxList(false, "");
            bench.drawPanelsKept();
            bench.panelPasses();
            String was = bench.spoilPanel();
            bench.panelPasses();
            List<String> kept = bench.panelSignature();
            int[][] keptPx = bench.panelPixels();
            bench.showFreshPanels();
            List<String> fresh = bench.panelSignature();
            int[][] freshPx = bench.panelPixels();
            bench.restoreKeptPanels();
            bench.drawPanelsKept();
            bench.panelPasses();
            List<String> mended = bench.panelSignature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx[0], freshPx[0]), firstDifference(mended, fresh)};
        });
        check("one figure made wrong in the kept city panel (\"" + spoilt[0] + "\") parts its signature from the new panels'",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        // What the rebuild did by being a rebuild, put back by hand: the scroll, the look under a still pointer, the focus.
        double[] scroll = fx(() -> {
            bench.cityMode(true, false);
            bench.sectionsAll(true);
            bench.drawPanelsKept();
            bench.panelPasses();
            javafx.scene.control.ScrollPane before = bench.cityScroller();
            before.setVvalue(.5);
            bench.panelPasses();
            double at = before.getVvalue();
            month(g);
            bench.drawPanelsKept();
            bench.panelPasses();
            return new double[]{bench.cityScroller() == before ? 1 : 0, at, bench.cityScroller().getVvalue()};
        });
        check("the city panel's scroller is kept and stays where it was put (" + String.format("%.2f", scroll[1]) + " then "
                + String.format("%.2f", scroll[2]) + ")", scroll[0] == 1 && scroll[1] > .1 && Math.abs(scroll[2] - scroll[1]) < .05);

        String[] looks = fx(() -> {
            bench.cityMode(false, false);       // Summary: rows that go to the tab that owns the number
            bench.drawPanelsKept();
            bench.panelPasses();
            javafx.scene.Node row = bench.firstHoverInCityPanel();
            if (row == null) return new String[]{"", "", "", ""};
            String rest = row.getStyle();
            row.getOnMouseEntered().handle(null);
            String hover = row.getStyle();
            row.getOnMouseExited().handle(null);
            KeptBench.hover(row, true);
            bench.drawPanelsKept();
            String under = bench.firstHoverInCityPanel().getStyle();
            KeptBench.hover(row, false);
            bench.drawPanelsKept();
            return new String[]{rest, hover, under, bench.firstHoverInCityPanel().getStyle()};
        });
        check("a summary row drawn again under the pointer keeps its hover look, and loses it when the pointer goes",
                !looks[0].isEmpty() && !looks[0].equals(looks[1]) && looks[2].equals(looks[1]) && looks[3].equals(looks[0]));

        Object[] hand = fx(() -> {
            // An unsettled notice, unfolded: it has the button that deals with it.
            Notice open = add(g, "keptcheck-f", "Kept check focus");
            bench.inboxList(true, "keptcheck-f");
            bench.drawPanelsKept();
            bench.panelPasses();
            boolean had = bench.focusInboxButton();
            bench.drawPanelsKept();
            boolean still = bench.inboxHasFocus();
            g.getInbox().all().remove(open);
            return new Object[]{had, still};
        });
        if (!(boolean) hand[0]) System.out.println("  the inbox list has no action button to take the focus here: the focus check is skipped");
        else check("an inbox button that had the focus loses it at the next kept draw, as the rebuild took it", !(boolean) hand[1]);

        System.out.println("\n--- 13: what the whole chrome costs, kept ---");
        double[] cost = fx(() -> {
            bench.cityMode(true, false);
            bench.sectionsAll(false);
            bench.section("econ", true);
            bench.inboxList(true, "");
            bench.clock(true, null);
            bench.speed(3);
            bench.screen(CHROME_SCREENS[0]);
            bench.drawChromeKept();
            bench.drawPanelsKept();
            bench.chromePasses();
            bench.panelPasses();
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawChromeKept() + bench.drawPanelsKept();
                double[] c = bench.chromePasses(), p = bench.panelPasses();
                kept.add(draw + c[0] + c[1] + p[0] + p[1]);
            }
            // The same month built new: every node of the header, the rail and the three panels made again.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] a = bench.showFreshChrome(), b = bench.showFreshPanels();
                fresh.add(a[0] + a[1] + a[2] + b[0] + b[1] + b[2]);
                bench.restoreKeptChrome();
                bench.restoreKeptPanels();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the whole chrome's month (its draws, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_ALL_CHROME_MS);
        String timed = "the whole kept chrome's month is within KEPT_ALL_CHROME_MS (" + KEPT_ALL_CHROME_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;
    }

    /**
     * The budget for the kept City History month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC:
     * twice the kept month's median, rounded up (5.0 to 8.2 ms in five runs, 6.2 between them; the same month built new
     * 20.6 to 26.3 ms).
     */
    static final double KEPT_HISTORY_MS = 13;

    /** A hand on City History's choices: what it does to the bench, and why it cannot be shown (null when it can). */
    @FunctionalInterface
    interface Hand {
        String on(KeptBench bench) throws Exception;
    }

    /**
     * One choice that reshapes the page: its name and the hand that makes it; `opens` for a fold opened, which has to add to the page (KP2);
     * `acts` for a choice that makes the model act (KP3: automatic building turned on orders what the Overview advises, so the next month
     * puts other buildings in its advice tiles and its status lines): the page it leaves is another page and not the same one redrawn, so
     * the share of its nodes handed back is printed and not asserted - the redraw after it is equal to a page built new all the same.
     */
    record Choice(String name, Hand hand, boolean opens, boolean acts) {
        Choice(String name, Hand hand, boolean opens) {
            this(name, hand, opens, false);
        }

        Choice(String name, Hand hand) {
            this(name, hand, false, false);
        }
    }

    /** A choice that makes the model act (see Choice.acts). */
    static Choice acting(String name, Hand hand) {
        return new Choice(name, hand, false, true);
    }

    /** The first of these lines picked and not in the preset the page opens on: a line to tick and untick. */
    static final String TICKED = "unemployment";

    /**
     * Sections 14 to 20: City History kept (KP1). The same city, played on; the bench draws the page
     * kept from its pool and built new (the pools swapped for empty ones), the two charts shared by both.
     */
    static void history(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 14: City History is kept ---");
        // The first draw makes the page; it is laid out (to where the scroller's width settles the charts' size) before the months start.
        int firstMade = fx(() -> {
            bench.drawHistoryKept();
            for (int i = 0; i < 3; i++) bench.passes();
            return bench.historyCounts()[0];
        });
        List<javafx.scene.Node> charts = fx(bench::historyCharts);
        List<javafx.scene.Node> chartParents = fx(() -> {
            List<javafx.scene.Node> parents = new ArrayList<>();
            for (javafx.scene.Node c : charts) parents.add(c.getParent());
            return parents;
        });
        List<javafx.scene.Node> legend = fx(bench::historyLegend), ranges = fx(bench::historyRanges);
        System.out.printf("  the first draw made %d nodes through Kept; the page holds %d charts, a legend of %d chips and %d range buttons%n",
                firstMade, charts.size(), legend.size(), ranges.size());
        check("the page holds the big chart and the two pins' (" + charts.size() + " charts)", charts.size() == 3);
        int watched = fx(bench::watchGraphics);
        int sameTop = 0, quiet = 0, madeMost = 0, chartsKept = 0, chipsKept = 0, graphicMoves = 0;
        long handedBack = 0;
        for (int m = 1; m <= MONTHS; m++) {
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.historyTop();
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                int[] c = bench.historyCounts();
                boolean chartsStay = bench.historyCharts().equals(charts);
                for (int i = 0; chartsStay && i < charts.size(); i++) {
                    chartsStay = charts.get(i).getScene() != null && charts.get(i).getParent() == chartParents.get(i);
                }
                boolean chipsStay = bench.historyLegend().equals(legend) && bench.historyRanges().equals(ranges);
                return new Object[]{before.equals(bench.historyTop()), c[0], c[1], chartsStay, chipsStay, bench.graphicMoves()};
            });
            if ((boolean) r[0]) sameTop++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            handedBack += (int) r[2];
            if ((boolean) r[3]) chartsKept++;
            if ((boolean) r[4]) chipsKept++;
            graphicMoves += (int) r[5];
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, handedBack / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the page it drew (the same top nodes, " + sameTop + " of " + MONTHS + ")", sameTop == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + handedBack / MONTHS + " of "
                + firstMade + ")", handedBack / MONTHS * 10 >= firstMade * 9L);
        check("most months make no node at all (" + quiet + " of " + MONTHS + ")", quiet * 2 > MONTHS);
        check("the big chart and both pins' stay in the scene, in the same box, after every redraw (" + chartsKept + " of " + MONTHS + ")",
                chartsKept == MONTHS);
        check("the legend's chips and the range buttons are the same nodes after every redraw (" + chipsKept + " of " + MONTHS + ")",
                chipsKept == MONTHS);
        check("no label's graphic is taken off the scene and put back by a kept redraw (" + watched + " watched, " + graphicMoves + " moves)",
                watched >= 3 && graphicMoves == 0);

        System.out.println("\n--- 15 and 16: the kept page, month after month, against a page built new, and its pixels ---");
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        int differ = 0, shotsSame = 0, shotsAlike = 0;
        String firstDiffer = null;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                bench.settle();
                bench.finishPageAnimations();
                List<String> kept = bench.signature();
                int[] keptPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.showFreshHistory();
                bench.settle();
                bench.finishPageAnimations();
                List<String> fresh = bench.signature();
                int[] freshPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.restoreKeptHistory();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[0];
            }
            if (r[1] != null) {
                int[] a = (int[]) r[1], b = (int[]) r[2];
                if (java.util.Arrays.equals(a, b)) shotsSame++;
                if (alike(a, b)) shotsAlike++;
                if (!java.util.Arrays.equals(a, b)) {
                    System.out.println("  pixels differ at month " + g.getMonth() + ": " + pixelDifference(a, b));
                    String to = System.getProperty("keptcheck.out");
                    if (to != null) {
                        png(a, java.nio.file.Path.of(to, "history-kept-m" + g.getMonth() + ".png"));
                        png(b, java.nio.file.Path.of(to, "history-new-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept page's signature equals the new page's at every month (" + differ + " differ)", differ == 0);
        System.out.printf("  %d of %d months the very same pixels%n", shotsSame, shotAt.size());
        check("the kept page and the new page are the same picture at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + ")", shotsAlike == shotAt.size());

        // The fixture that causes a difference: one figure on the kept page made wrong by hand.
        Object[] spoilt = fx(() -> {
            bench.drawHistoryKept();
            bench.passes();
            String was = bench.spoilHistory();
            bench.settle();
            List<String> kept = bench.signature();
            int[] keptPx = bench.pixels();
            bench.showFreshHistory();
            bench.settle();
            List<String> fresh = bench.signature();
            int[] freshPx = bench.pixels();
            bench.restoreKeptHistory();
            bench.drawHistoryKept();
            bench.settle();
            List<String> mended = bench.signature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx, freshPx), firstDifference(mended, fresh)};
        });
        check("one figure made wrong on the kept page (\"" + spoilt[0] + "\") parts its signature from the new page's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        System.out.println("\n--- 17: what reshapes the page ---");
        int[] bounds = fx(bench::historyBounds);
        double[] episode = fx(bench::historyEpisodeSpan);
        String population = "population";
        String peopleGroup = fx(() -> bench.historyGroupOf(population));
        List<Choice> choices = new ArrayList<>(List.of(
                new Choice("a line picked", b -> { b.historyPick(TICKED, true); return null; }),
                new Choice("a line unpicked", b -> { b.historyPick(TICKED, false); return null; }),
                new Choice("a preset (the budget)", b -> { b.historyPreset(3); return null; }),
                new Choice("another preset (the people)", b -> { b.historyPreset(2); return null; }),
                new Choice("a picker group closed", b -> { b.historyGroup(peopleGroup, false); return null; }),
                new Choice("a picker group opened", b -> { b.historyGroup(peopleGroup, true); return null; }),
                new Choice("a word typed in the filter", b -> b.historyType("rate") ? null : "the page has no filter box"),
                new Choice("the filter cleared", b -> b.historyType("") ? null : "the page has no filter box"),
                new Choice("the hard times' fold opened", b -> { b.historyFold("hard times", true); return null; }),
                new Choice("the hard times' fold closed", b -> { b.historyFold("hard times", false); return null; }),
                new Choice("the goods' fold opened", b -> { b.historyFold("goods", true); return null; }),
                new Choice("the goods' fold closed", b -> { b.historyFold("goods", false); return null; }),
                new Choice("the log scale on", b -> {
                    b.historyPickOnly(population);
                    return nullIfOffered(b.historyLog(true), "log");
                }),
                new Choice("the log scale off", b -> { b.historyLog(false); return null; }),
                new Choice("real GDP alone, in layers", b -> {
                    b.historyPickOnly("realGdp");
                    b.historyLayers(true);
                    return null;
                }),
                new Choice("the layers off", b -> { b.historyLayers(false); return null; }),
                new Choice("a line hidden in the legend", b -> {
                    // The legend shows the lines of the last draw: draw the preset's before a chip is pressed.
                    b.historyPreset(0);
                    b.drawHistoryKept();
                    b.passes();
                    return b.historyLegendClick(0) && b.historyHidden() == 1 ? null : "the legend's chip did not hide its line";
                }),
                new Choice("the line shown again", b -> b.historyLegendClick(0) && b.historyHidden() == 0 ? null : "the chip did not show its line"),
                new Choice("a range on the chart", b -> { b.historyRange(ham.citybuildersim.ChartModel.ALL); return null; }),
                new Choice("the first range on the chart", b -> { b.historyRange(ham.citybuildersim.ChartModel.RANGES[0]); return null; })));
        if (episode != null) {
            choices.add(new Choice("the window moved to an episode's span", b -> { b.historyWindow(episode[0], episode[1]); return null; }));
        } else {
            System.out.println("  this city has no named episode: the window is not moved to one");
        }
        choices.addAll(List.of(
                new Choice("the window moved by hand", b -> {
                    b.historyWindow(bounds[0] + 50, bounds[0] + 150);
                    return null;
                }),
                new Choice("a line pinned", b -> { b.historyPin(TICKED); return null; }),
                new Choice("a pin taken off", b -> { b.historyUnpin(false); return null; }),
                new Choice("the chart's full screen on", b -> { b.historyFull(true); return null; }),
                new Choice("the chart's full screen off", b -> { b.historyFull(false); return null; }),
                new Choice("the year book's card shown", b -> { b.historyBook("The year book was written (KeptCheck's card)."); return null; }),
                new Choice("the year book's card put away", b -> { b.historyBook(null); return null; })));
        for (Choice c : choices) {
            String d = fx(() -> {
                String premise = c.hand().on(bench);
                if (premise != null) return "the choice could not be made: " + premise;
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                bench.settle();
                bench.finishPageAnimations();
                List<String> kept = bench.signature();
                bench.showFreshHistory();
                bench.settle();
                bench.finishPageAnimations();
                List<String> fresh = bench.signature();
                bench.restoreKeptHistory();
                // ...and a month later, the page drawn over the reshaped one.
                month(g);
                bench.drawHistoryKept();
                bench.passes();
                bench.settle();
                bench.finishPageAnimations();
                List<String> kept2 = bench.signature();
                bench.showFreshHistory();
                bench.settle();
                bench.finishPageAnimations();
                List<String> fresh2 = bench.signature();
                bench.restoreKeptHistory();
                String a = firstDifference(kept, fresh), b = firstDifference(kept2, fresh2);
                return a != null ? a : b;
            });
            if (d != null) System.out.println("  " + c.name() + ": " + d);
            check(c.name() + ": the redraw and the next equal a page built new", d == null);
        }

        crosshair(g, bench);
        countUp(g, bench);
        historyCost(g, bench);
    }

    /** Months landed until the figure has moved (at most twelve): whether it did. */
    private static boolean landUntil(Game g, java.util.function.BooleanSupplier moved) {
        for (int i = 0; i < 12; i++) {
            month(g);
            if (moved.getAsBoolean()) return true;
        }
        return false;
    }

    /** The choice's refusal, as the premise it could not meet, or null when the chart is offered. */
    private static String nullIfOffered(String refusal, String what) {
        return refusal == null ? null : "the " + what + " scale is refused: " + refusal;
    }

    /**
     * Section 18: what a page built new loses and a kept one keeps. The window is the screen's (the
     * ChartModel), so the charts' own state is what a month could take: the crosshair under the pointer
     * (a chart clears it when its scene goes null, and a page built new takes the chart off the scene) and
     * a legend chip under it.
     */
    static void crosshair(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 18: the crosshair and the window survive a month ---");
        int range = ham.citybuildersim.ChartModel.DEFAULT_RANGE;
        // The window on its range follows the newest month, and one put by hand stays where it was put.
        Object[] win = fx(() -> {
            bench.historyPreset(0);
            bench.historyRange(range);
            bench.drawHistoryKept();
            bench.passes();
            int[] end = bench.historyBounds(), on = bench.historyWindow();
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            int[] end2 = bench.historyBounds(), followed = bench.historyWindow();
            // Put by hand, a stretch ending before the newest month.
            bench.historyWindow(end2[1] - 240, end2[1] - 120);
            bench.drawHistoryKept();
            bench.passes();
            int[] put = bench.historyWindow();
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            int[] end3 = bench.historyBounds(), stayed = bench.historyWindow();
            return new Object[]{end, on, end2, followed, put, end3, stayed};
        });
        int[] end = (int[]) win[0], on = (int[]) win[1], end2 = (int[]) win[2], followed = (int[]) win[3],
                put = (int[]) win[4], end3 = (int[]) win[5], stayed = (int[]) win[6];
        System.out.printf("  on its range: months %d-%d of %d, then %d-%d of %d; put by hand: %d-%d, then %d-%d of %d%n",
                on[0], on[1], end[1], followed[0], followed[1], end2[1], put[0], put[1], stayed[0], stayed[1], end3[1]);
        check("a month landed (the history grew by one)", end2[1] == end[1] + 1 && end3[1] == end2[1] + 1);
        check("a window on the newest month follows it across a kept redraw", on[1] == end[1] && followed[1] == end2[1]);
        check("a window put by hand, ending before the newest month, stays where it was across a kept redraw",
                put[1] < end2[1] && stayed[0] == put[0] && stayed[1] == put[1]);

        // The crosshair: a move of the pointer fired on the big chart's plot (a MOUSE_MOVED event, not a setter).
        Object[] cross = fx(() -> {
            bench.historyRange(range);
            bench.drawHistoryKept();
            bench.passes();
            boolean over = bench.historyHoverPlot();
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            boolean afterKept = bench.historyHovering();
            bench.showFreshHistory();
            boolean afterNew = bench.historyHovering();
            bench.restoreKeptHistory();
            return new Object[]{over, afterKept, afterNew};
        });
        check("the pointer over the big chart's plot shows its crosshair", (boolean) cross[0]);
        check("the crosshair is still there after a kept redraw", (boolean) cross[1]);
        check("...and a page built new, which takes the chart off the scene, clears it", !(boolean) cross[2]);

        // A legend chip under the pointer: the same chip, the same tooltip, still under it.
        Object[] chip = fx(() -> {
            bench.drawHistoryKept();
            bench.passes();
            javafx.scene.Node under = bench.historyLegend().get(0);
            Object tip = KeptBench.tooltipOf(under);
            KeptBench.hover(under, true);
            month(g);
            bench.drawHistoryKept();
            bench.passes();
            javafx.scene.Node now = bench.historyLegend().get(0);
            boolean kept = now == under && under.isHover() && KeptBench.tooltipOf(under) == tip && tip != null;
            bench.showFreshHistory();
            javafx.scene.Node fresh = bench.historyLegend().get(0);
            boolean freshIsNew = fresh != under && !fresh.isHover();
            bench.restoreKeptHistory();
            KeptBench.hover(under, false);
            return new Object[]{tip != null, kept, freshIsNew};
        });
        check("a legend chip has a tooltip to keep", (boolean) chip[0]);
        check("a legend chip under the pointer is still the same chip, still under it, with the same tooltip, after a kept redraw",
                (boolean) chip[1]);
        check("...and a page built new has a chip of its own, with no pointer on it", (boolean) chip[2]);
    }

    /**
     * Section 19: the count-up is for a clock that can be followed. A month that lands on a page watched
     * starts the figure's count-up when UserInterface.countsUp() allows - paused, or no faster than
     * COUNT_UP_TOP - and sets it at once above. The real show methods are driven (People's and City
     * History's), the clock's rung set on the window as the speed arrows set it, the page redrawn kept.
     */
    static void countUp(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 19: the count-up only at 1x-10x ---");
        double[] ladder = KeptBench.speedLadder();
        double top = KeptBench.countUpTop();
        StringBuilder wrongPeople = new StringBuilder(), wrongPin = new StringBuilder();
        int countingRungs = 0;
        for (double speed : ladder) if (speed <= top) countingRungs++;
        System.out.printf("  the ladder has %d rungs, %d of them at or under COUNT_UP_TOP (%.0fx)%n", ladder.length, countingRungs, top);

        // People: the headcount on LIVING HERE.
        fx(() -> {
            bench.clock(true, null);
            bench.speed(0);
            bench.screen("showHistoryMenu");
            bench.forgetPeople();
            bench.showPeople();
            return null;
        });
        for (int rung = 0; rung <= ladder.length; rung++) {
            // The last pass is the clock stopped, at the top rung: a month stepped by hand counts at any speed.
            boolean paused = rung == ladder.length;
            int at = paused ? ladder.length - 1 : rung;
            boolean expect = paused || ladder[at] <= top;
            String result = fx(() -> {
                bench.clock(!paused, null);
                bench.speed(at);
                bench.showPeople();
                bench.finishPageAnimations();
                Animation before = bench.peopleCount();
                if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
                bench.showPeople();
                Animation after = bench.peopleCount();
                boolean started = after != null && after != before;
                return started == expect ? null : (started ? "counted" : "did not count") + " at " + ladder[at] + "x"
                        + (paused ? " paused" : "") + ", where it " + (expect ? "should" : "should not");
            });
            if (result != null) wrongPeople.append(wrongPeople.length() == 0 ? "" : "; ").append(result);
        }
        if (wrongPeople.length() > 0) System.out.println("  People: " + wrongPeople);
        check("a month landing on People starts the headcount's count-up exactly at the rungs countsUp() allows, and when paused ("
                + (ladder.length + 1) + " clocks)", wrongPeople.length() == 0);

        // City History: the left pin's figure, pinned to the headcount - a line that moves most months, whose last month the redraw takes up.
        fx(() -> {
            bench.clock(true, null);
            bench.speed(0);
            bench.historyPinLeft("population");
            bench.forgetHistory();
            bench.showHistory();
            return null;
        });
        for (int rung = 0; rung <= ladder.length; rung++) {
            boolean paused = rung == ladder.length;
            int at = paused ? ladder.length - 1 : rung;
            boolean expect = paused || ladder[at] <= top;
            String result = fx(() -> {
                bench.clock(!paused, null);
                bench.speed(at);
                bench.showHistory();
                bench.finishPageAnimations();
                Animation before = bench.pinCount();
                if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
                bench.showHistory();
                Animation after = bench.pinCount();
                boolean started = after != null && after != before;
                return started == expect ? null : (started ? "counted" : "did not count") + " at " + ladder[at] + "x"
                        + (paused ? " paused" : "") + ", where it " + (expect ? "should" : "should not");
            });
            if (result != null) wrongPin.append(wrongPin.length() == 0 ? "" : "; ").append(result);
        }
        if (wrongPin.length() > 0) System.out.println("  City History: " + wrongPin);
        check("a month landing on City History starts the pin's count-up exactly at the rungs countsUp() allows, and when paused ("
                + (ladder.length + 1) + " clocks)", wrongPin.length() == 0);

        // A count-up still running when the clock has been sped past the top: the next month sets the figure at once and stops it.
        String stopped = fx(() -> {
            int slow = 0;
            while (ladder[slow] > top) slow++;
            bench.clock(true, null);
            bench.speed(slow);
            bench.showHistory();
            bench.finishPageAnimations();
            if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
            bench.showHistory();
            Animation running = bench.pinCount();
            if (running == null || running.getStatus() == Animation.Status.STOPPED) return "no count-up was running to stop";
            bench.speed(ladder.length - 1);
            if (!landUntil(g, bench::headcountMoved)) return "no month in twelve moved the headcount";
            bench.showHistory();
            String[] figures = bench.pinFigures();
            boolean left = running.getStatus() == Animation.Status.STOPPED && java.util.Objects.equals(figures[0], figures[1]);
            return left ? null : "the pin's count-up is " + running.getStatus() + " and the figure reads " + figures[0] + ", not " + figures[1];
        });
        if (stopped != null) System.out.println("  " + stopped);
        check("a count-up running when a month lands past COUNT_UP_TOP is stopped, and the figure is the month's", stopped == null);
    }

    /** Section 20: what the kept City History month costs, against the same month built new. */
    static void historyCost(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 20: what the kept City History month costs ---");
        double[] cost = fx(() -> {
            // The page as it opens: the first preset, the default range, nothing folded open.
            bench.historyPreset(0);
            bench.historyRange(ham.citybuildersim.ChartModel.DEFAULT_RANGE);
            bench.drawHistoryKept();
            for (int i = 0; i < 3; i++) bench.passes();
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawHistoryKept();
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before KP1.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFreshHistory();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptHistory();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_HISTORY_MS);
        String timed = "the kept City History month is within KEPT_HISTORY_MS (" + KEPT_HISTORY_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;
    }

    /* =====================================================================
       INFRASTRUCTURE, SERVICES AND THE LAND OFFICE (KP2), sections 21 to 35: each page kept, shown as a page
       built new shows it, kept right through what reshapes it, and cheap; then the controls and the hover.
       ===================================================================== */

    /** The share of a page's nodes a month's redraw hands back, at least: nine in ten (the People page's rule). */
    static final double HANDED_BACK = 0.9;

    /** The page as it stands (drawn kept, and laid out) against the same page built new: the first difference, or null. The animations a draw started are run to their ends first and the page laid out after, as the pulse does: a count-up's last word is another width. */
    static String againstNew(KeptBench bench, KeptBench.Page page) throws Exception {
        return againstNew(bench, page, false);
    }

    /**
     * ...and, for a page with pictures painted on canvases, which no signature holds (KP7, the Rankings table), the canvases' pixels too: the signature's
     * difference first, else the canvases' (no pixel more than SHADE_LEVELS apart, and at most SHADED_SHARE of them apart at all). The canvases alone
     * (KeptBench.canvasPixels()): the rest of the page is the signature's, and a snapshot of the whole page at every choice wore out the renderer's
     * texture pool on the Rankings tab's long list.
     */
    static String againstNew(KeptBench bench, KeptBench.Page page, boolean pixels) throws Exception {
        bench.passes();
        bench.finishPageAnimations();
        bench.settle();
        List<String> kept = bench.signature();
        int[] keptPx = pixels ? bench.canvasPixels() : null;
        bench.showFresh(page);
        bench.finishPageAnimations();
        bench.settle();
        List<String> fresh = bench.signature();
        int[] freshPx = pixels ? bench.canvasPixels() : null;
        bench.restoreKeptPage();
        String d = firstDifference(kept, fresh);
        if (d == null && pixels && !alike(keptPx, freshPx)) d = "the pixels differ: " + pixelDifference(keptPx, freshPx);
        return d;
    }

    /** Section n: the page is kept - a month's redraw keeps its top nodes, hands back nine in ten of the first draw's nodes, and on most months makes none. */
    static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception {
        pageKept(g, bench, page, n, false);
    }

    /**
     * ...for a page whose content is the model's events (`eventful`, KP5: the city's fund, where a bond repaid, a payment and a quote line are rows
     * of their own): the nodes a month makes are the month's new rows, so most months making none is not the measure; no month making more than a
     * tenth of the nodes the first draw made is, and the months that made none are printed.
     */
    static void pageKept(Game g, KeptBench bench, KeptBench.Page page, int n, boolean eventful) throws Exception {
        System.out.println("\n--- " + n + ": " + page.name() + " is kept ---");
        int firstMade = fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            for (int i = 0; i < 3; i++) bench.passes();
            return bench.counts(page)[0];
        });
        System.out.printf("  the first draw made %d nodes through Kept%n", firstMade);
        int sameTop = 0, quiet = 0, madeMost = 0;
        long handedBack = 0;
        for (int m = 1; m <= MONTHS; m++) {
            Object[] r = fx(() -> {
                List<javafx.scene.Node> before = bench.pageTop();
                month(g);
                bench.drawKept(page);
                bench.passes();
                int[] c = bench.counts(page);
                return new Object[]{before.equals(bench.pageTop()), c[0], c[1]};
            });
            if ((boolean) r[0]) sameTop++;
            if ((int) r[1] == 0) quiet++;
            madeMost = Math.max(madeMost, (int) r[1]);
            handedBack += (int) r[2];
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, handedBack / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the page it drew (the same top nodes, " + sameTop + " of " + MONTHS + ")", sameTop == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + handedBack / MONTHS + " of "
                + firstMade + ")", handedBack / MONTHS * 10 >= firstMade * 9L);
        if (eventful) {
            check("no month makes more than a tenth of the nodes the first draw made (the most made in one: " + madeMost + " of " + firstMade
                    + "; " + quiet + " of " + MONTHS + " months made none)", madeMost * 10 <= firstMade);
        } else {
            check("most months make no node at all (" + quiet + " of " + MONTHS + ")", quiet * 2 > MONTHS);
        }
    }

    /** Section n: the kept page shows what the page built new shows, month after month, to the pixel at three months, and a figure made wrong is seen and set right. */
    static void pageShows(Game g, KeptBench bench, KeptBench.Page page, int n) throws Exception {
        System.out.println("\n--- " + n + ": " + page.name() + ", the kept page against a page built new, and its pixels ---");
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        String tag = page.name().replace(' ', '-');
        int differ = 0, shotsSame = 0, shotsAlike = 0;
        String firstDiffer = null;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                month(g);
                bench.drawKept(page);
                bench.passes();
                bench.finishPageAnimations();
                bench.settle();
                List<String> kept = bench.signature();
                int[] keptPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.showFresh(page);
                bench.finishPageAnimations();
                bench.settle();
                List<String> fresh = bench.signature();
                int[] freshPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.restoreKeptPage();
                return new Object[]{firstDifference(kept, fresh), keptPx, freshPx};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[0];
            }
            if (r[1] != null) {
                int[] a = (int[]) r[1], b = (int[]) r[2];
                if (java.util.Arrays.equals(a, b)) shotsSame++;
                if (alike(a, b)) shotsAlike++;
                if (!java.util.Arrays.equals(a, b)) {
                    System.out.println("  pixels differ at month " + g.getMonth() + ": " + pixelDifference(a, b));
                    String to = System.getProperty("keptcheck.out");
                    if (to != null) {
                        png(a, java.nio.file.Path.of(to, tag + "-kept-m" + g.getMonth() + ".png"));
                        png(b, java.nio.file.Path.of(to, tag + "-new-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept page's signature equals the new page's at every month (" + differ + " differ)", differ == 0);
        System.out.printf("  %d of %d months the very same pixels%n", shotsSame, shotAt.size());
        check("the kept page and the new page are the same picture at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + ")", shotsAlike == shotAt.size());

        // The fixture that causes a difference: one figure on the kept page made wrong by hand.
        Object[] spoilt = fx(() -> {
            bench.drawKept(page);
            bench.passes();
            String was = bench.spoilPage();
            bench.settle();
            List<String> kept = bench.signature();
            int[] keptPx = bench.pixels();
            bench.showFresh(page);
            bench.settle();
            List<String> fresh = bench.signature();
            int[] freshPx = bench.pixels();
            bench.restoreKeptPage();
            bench.drawKept(page);
            bench.settle();
            List<String> mended = bench.signature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx, freshPx), firstDifference(mended, fresh)};
        });
        check("one figure made wrong on the kept page (\"" + spoilt[0] + "\") parts its signature from the new page's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);
    }

    /**
     * Section n: every choice that reshapes the page, made in turn: the redraw after it and the one after a month lands are
     * each equal to a page built new, and the month's redraw hands back nine in ten of the page's nodes. A fold opened
     * has to add to the page (the premise: the fold is on it).
     */
    static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices) throws Exception {
        pageReshaped(g, bench, page, n, choices, HANDED_BACK);
    }

    /** ...with the share of the page a month's redraw has to hand back set by the caller (KP6: KP6_HANDED_BACK). */
    static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, double share) throws Exception {
        pageReshaped(g, bench, page, n, choices, share, false);
    }

    /** ...and, with `pixels`, each redraw's picture against the page built new's as well (KP7: the Rankings table is painted, and no signature holds it). */
    static void pageReshaped(Game g, KeptBench bench, KeptBench.Page page, int n, List<Choice> choices, double share, boolean pixels) throws Exception {
        System.out.println("\n--- " + n + ": what reshapes " + page.name() + (pixels ? ", to the pixel" : "") + " ---");
        int size = fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            bench.passes();
            bench.settle();
            return bench.signature().size();
        });
        double worst = Double.MAX_VALUE;
        String worstAt = "";
        int worstMade = 0, worstKept = 0;
        for (Choice c : choices) {
            Object[] r = fx(() -> {
                String premise = c.hand().on(bench);
                if (premise != null) return new Object[]{"the choice could not be made: " + premise, 0, 1.0, 0, 0};
                bench.drawKept(page);
                String a = againstNew(bench, page, pixels);
                int grown = bench.signature().size();
                month(g);
                bench.drawKept(page);
                int[] counts = bench.counts(page);
                String b = againstNew(bench, page, pixels);
                return new Object[]{a != null ? a : b, grown, counts[1] / (double) Math.max(1, counts[0] + counts[1]), counts[0], counts[1]};
            });
            String d = (String) r[0];
            if (d != null) System.out.println("  " + c.name() + ": " + d);
            check(c.name() + ": the redraw and the next equal a page built new", d == null);
            if (c.opens()) {
                check(c.name() + " adds to the page (" + size + " lines, then " + r[1] + ")", (int) r[1] > size);
            }
            size = (int) r[1] == 0 ? size : (int) r[1];
            if (c.acts()) {
                System.out.printf("  %s: the month after handed back %.1f%% of the page's nodes (the model acted: not counted below)%n", c.name(), (double) r[2] * 100);
            } else if ((double) r[2] < worst) {
                worst = (double) r[2];
                worstAt = c.name();
                worstMade = (int) r[3];
                worstKept = (int) r[4];
            }
        }
        System.out.printf("  the month's redraw after a choice handed back %.1f%% of the page's nodes at the fewest (after \"%s\": %d made, %d handed back)%n",
                worst * 100, worstAt, worstMade, worstKept);
        check("a month's redraw after each choice hands back " + (share == HANDED_BACK ? "nine in ten" : String.format("%.0f%%", share * 100)) + " of the page's nodes, or more", worst >= share);
    }

    /** Section n: what the kept month on the page's home costs, against the same month built new. */
    static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial) throws Exception {
        pageCost(g, bench, page, n, budget, dial, 9);
    }

    /** ...the median of `samples` months, for the pages whose month is long (the fund's, tens of thousands of nodes). */
    static void pageCost(Game g, KeptBench bench, KeptBench.Page page, int n, double budget, String dial, int samples) throws Exception {
        System.out.println("\n--- " + n + ": what the kept month on " + page.name() + " costs ---");
        double[] cost = fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            for (int i = 0; i < 3; i++) bench.passes();
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < samples; i++) {
                month(g);
                double draw = bench.drawKept(page);
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before KP2.
            for (int i = 0; i < samples; i++) {
                month(g);
                double[] f = bench.showFresh(page);
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKeptPage();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of %d: kept %.1f ms, built new %.1f ms%n", samples, cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], budget);
        String timed = "the kept month on " + page.name() + " is within " + dial + " (" + budget + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;
    }

    /** A choice with no side effect on the model: a hand that changes the screen's state only. */
    static Choice choice(String name, Hand hand) {
        return new Choice(name, hand);
    }

    /**
     * The budget for the kept Infrastructure month - its draw, then the pulse's CSS and layout, on the Roads page - at CITY_MONTHS, on
     * the PC: twice the kept month's median, rounded up (2.7 to 6.3 ms in five runs, 4.4 between them; the same month built new
     * 10.2 to 19.1 ms).
     */
    static final double KEPT_INFRASTRUCTURE_MS = 9;

    /**
     * The budget for the kept Services month - its draw, then the pulse's CSS and layout, on the Health overview - at CITY_MONTHS, on
     * the PC: twice the kept month's median, rounded up (4.8 to 10.5 ms in five runs, 6.5 between them; the same month built new
     * 9.9 to 19.5 ms).
     */
    static final double KEPT_SERVICES_MS = 13;

    /**
     * The budget for the kept land office month - its draw, then the pulse's CSS and layout, on the office as it opens - at CITY_MONTHS,
     * on the PC: twice the kept month's median, rounded up (2.1 to 3.3 ms in five runs, 2.4 between them; the same month built new
     * 9.1 to 16.0 ms).
     */
    static final double KEPT_LAND_MS = 5;

    /** Transit built from the model's own types: a Bus Network ordered if the city has none, a month played; what it found. */
    static String ensureTransit(Game g) throws Exception {
        InfrastructureManager roads = g.getInfrastructureManager();
        if (roads.getTransitCapacity() > 0) return String.format("the city has transit already (%.0f seats, %.0f riders)", roads.getTransitCapacity(), roads.getTransitRiders());
        BuildingsTemplate bus = g.getBuildingManager().getTemplateByName("Bus Network");
        quietly(() -> g.buildStack(bus, 2, true));
        month(g);
        month(g);
        return String.format("the city had no transit: two Bus Networks ordered (%.0f seats, %.0f riders after two months)", roads.getTransitCapacity(), roads.getTransitRiders());
    }

    /** Sections 21 to 24: Infrastructure kept. */
    static void infrastructure(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== Infrastructure (KP2) ===");
        System.out.println("  " + fx(() -> ensureTransit(g)));
        KeptBench.Page page = fx(bench::infrastructure);
        pageKept(g, bench, page, 21);
        pageShows(g, bench, page, 22);
        List<Choice> choices = List.of(
                new Choice("the roads' fold opened", b -> { b.infraFold("roads", true); return null; }, true),
                new Choice("the roads' fold closed", b -> { b.infraFold("roads", false); return null; }),
                choice("the Transit page", b -> { b.infraOpen("Transit", "All"); return null; }),
                new Choice("the transit fold opened", b -> { b.infraFold("transit", true); return null; }, true),
                new Choice("the transit fold closed", b -> { b.infraFold("transit", false); return null; }),
                choice("the Freight page", b -> { b.infraOpen("Freight", "All"); return null; }),
                new Choice("the freight fold opened", b -> { b.infraFold("freight", true); return null; }, true),
                new Choice("the freight fold closed", b -> { b.infraFold("freight", false); return null; }),
                choice("Freight on its Haulage division", b -> { b.infraOpen("Freight", "Haulage"); return null; }),
                choice("Freight on its Rail division", b -> { b.infraOpen("Freight", "Rail"); return null; }),
                new Choice("the railway fold opened", b -> { b.infraFold("railway", true); return null; }, true),
                new Choice("the railway fold closed", b -> { b.infraFold("railway", false); return null; }),
                choice("the Ports page", b -> { b.infraOpen("Ports", "All"); return null; }),
                choice("the Roads page again", b -> { b.infraOpen("Roads", "All"); return null; }));
        pageReshaped(g, bench, page, 23, choices);
        pageCost(g, bench, page, 24, KEPT_INFRASTRUCTURE_MS, "KEPT_INFRASTRUCTURE_MS");
    }

    /** The folds each sub-page of Services has, by "system/page" (a page not named has none). */
    static final String[][] SERVICES_FOLDS = {
            {"Health/Overview", "health:sick"}, {"Health/General care", "health:general"},
            {"Health/Childcare", "health:CHILDCARE"}, {"Health/Senior care", "health:SENIOR"},
            {"Health/Death care", "health:death"}, {"Health/Books", "books:health"},
            {"Education/Basic ladder", "education:ladder"}, {"Education/College", "education:COLLEGE"},
            {"Education/University", "education:UNIVERSITY"}, {"Education/Professions", "education:professions"},
            {"Education/Books", "books:education"},
            {"Utilities/Overview", "utilities:power", "utilities:water"}, {"Utilities/Books", "books:utilities"},
            {"Safety/Overview", "safety:causes"}, {"Safety/Police", "safety:police"}, {"Safety/Prisons", "safety:prisons"},
            {"Safety/Books", "books:safety"}};

    /** Sections 25 to 28: Services kept, every system's every sub-page and every fold on it. */
    static void services(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== Services (KP2) ===");
        KeptBench.Page page = fx(bench::services);
        pageKept(g, bench, page, 25);
        pageShows(g, bench, page, 26);
        List<Choice> choices = new ArrayList<>();
        for (String[] pageOf : KeptBench.servicesPages()) {
            String area = pageOf[0], sub = pageOf[1];
            choices.add(choice(area + ", " + sub, b -> { b.servicesOpen(area, sub); return null; }));
            for (String[] row : SERVICES_FOLDS) {
                if (!row[0].equals(area + "/" + sub)) continue;
                for (int k = 1; k < row.length; k++) {
                    String key = row[k];
                    choices.add(new Choice(area + ", " + sub + ": the fold " + key + " opened", b -> { b.servicesFold(key, true); return null; }, true));
                    choices.add(new Choice(area + ", " + sub + ": the fold " + key + " closed", b -> { b.servicesFold(key, false); return null; }));
                }
            }
        }
        pageReshaped(g, bench, page, 27, choices);
        pageCost(g, bench, page, 28, KEPT_SERVICES_MS, "KEPT_SERVICES_MS");
    }

    /** Sections 29 to 33: the land office kept - its sides, places, count, fold and way of paying, a plot bought, the funding page and the page for when the city fell short. */
    static void land(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The land office (KP2) ===");
        KeptBench.Page page = fx(bench::land);
        Object[] has = fx(() -> new Object[]{bench.landListed(), bench.landLandmasses(), bench.landPaidFromVault(), bench.landWaiting()});
        System.out.printf("  %d offers listed; %d sectors waiting on ground; ground on a second landmass (the landmass table, kept since KP4 through SectorScreen.oilTable, as the oil industry's are): %s%n",
                has[0], has[3], has[1]);
        boolean vault = (boolean) has[2];
        pageKept(g, bench, page, 29);
        pageShows(g, bench, page, 30);
        List<Choice> choices = new ArrayList<>();
        for (int side = 0; side < 4; side++) {
            final int s = side;
            choices.add(choice("side " + side + " picked", b -> { b.landSide(s); return null; }));
        }
        for (int place = 0; place < 6; place++) {
            final int p = place;
            choices.add(choice("the offer at place " + place + " picked", b -> { b.landPlace(p); return null; }));
        }
        choices.add(choice("no offer picked", b -> { b.landPlace(-1); return null; }));
        choices.add(choice("the best 1 to buy", b -> { b.landCount(1); return null; }));
        choices.add(choice("the best 2 to buy", b -> { b.landCount(2); return null; }));
        choices.add(choice("the best 5 to buy", b -> { b.landCount(5); return null; }));
        choices.add(new Choice("the details fold opened", b -> { b.landDetails(true); return null; }, true));
        choices.add(new Choice("the details fold closed", b -> { b.landDetails(false); return null; }));
        choices.add(choice("paid from the vault", b -> { b.landPay(true); return null; }));
        choices.add(choice("paid by converting cash", b -> { b.landPay(false); return null; }));
        choices.add(choice("the way of paying put back", b -> { b.landPay(vault); return null; }));
        choices.add(choice("a plot bought (its receipt, the plots that came onto the shelf)", b -> quietly(b::landBuy)));
        pageReshaped(g, bench, page, 31, choices);
        pageCost(g, bench, page, 32, KEPT_LAND_MS, "KEPT_LAND_MS");

        // The two other pages of the office, each an arrival that forgets the pool: drawn, then a month later. The funding page
        // needs a city that cannot pay for the best offer: the city is brought there by buying through the model (the last
        // of these sections, since it leaves the city poorer).
        System.out.println("\n--- 33: the land office's other pages ---");
        int bought = fx(() -> quietly(() -> bench.landBuyUntilShort(80)));
        List<Integer> ids = fx(bench::landFundingIds);
        System.out.println(bought >= 0 ? "  " + bought + " offers bought through the model until the city fell short of the best one"
                : "  80 offers bought and the city still pays for the best one");
        List<KeptBench.Page> others = new ArrayList<>();
        if (ids != null) others.add(fx(() -> bench.landFunding(ids)));
        else System.out.println("  the city can pay for the whole shelf: there is no funding page to draw");
        others.add(fx(() -> bench.landFellShort("bond", 1, 3)));
        others.add(fx(() -> bench.landFellShort(null, 0, 2)));
        for (KeptBench.Page other : others) {
            // (In the game the fell-short page is never redrawn over itself: a month landing draws the office. It is drawn so here, as every page is.)
            pageReshaped(g, bench, other, 33, List.of(choice(other.name() + " opened", b -> null)));
        }
    }

    /**
     * Section 34: a control keeps the player's hand. Transit's fare dial, through the screen's real show method: pressed, a
     * slider is held and a month landing under it leaves its thumb, its reading and its ladder's shown value where the player
     * put them; released, it stages the thumb and the page is a page built new; a value set outside any draw reaches the
     * reading of the latest draw. And City History's filter box keeps its text, its caret and its focus.
     */
    static void controls(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 34: a control keeps the player's hand ---");
        KeptBench.Page page = fx(bench::infrastructure);
        Object[] fare = fx(() -> {
            bench.home(page);
            bench.infraOpen("Transit", "All");
            bench.showInfrastructure();
            bench.passes();
            javafx.scene.control.Slider bar = bench.slider();
            if (bar == null) return null;
            double step = bar.getBlockIncrement();
            double snapped = bar.getMin() + Math.round((bar.getValue() - bar.getMin()) / step) * step;
            double thumb = snapped + 2 * step <= bar.getMax() + 1e-9 ? snapped + 2 * step : snapped - 2 * step;
            String before = KeptBench.readingOf(bar).getText();
            // (a) pressed, dragged two steps, a month lands.
            KeptBench.press(bar);
            boolean heldPressed = KeptBench.held(bar);
            bar.setValueChanging(true);
            bar.setValue(thumb);
            String dragged = KeptBench.readingOf(bar).getText();
            month(g);
            bench.showInfrastructure();
            bench.passes();
            boolean sameBar = bench.slider() == bar;
            // The reading is in the month's money (the fare dial is a price at founding prices, shown at the level the
            // month expects): what a dial built at the thumb says this month is the reference, staged for the one build.
            double stagedBefore = bench.stagedFare();
            String kept = KeptBench.readingOf(bar).getText();
            double shown = KeptBench.shownBy(bar);
            boolean heldStill = KeptBench.held(bar);
            double valueNow = bar.getValue();
            bench.stageFare(thumb);
            bench.showFresh(page);
            String atThumb = KeptBench.readingOf(bench.slider()).getText();
            bench.restoreKeptPage();
            bench.unstageFare();
            Object[] a = {heldPressed, sameBar, valueNow, thumb, kept, dragged, before, shown, heldStill, stagedBefore, atThumb};
            // (b) released: the thumb is staged, the screen redrawn, the page the page built new.
            bar.setValueChanging(false);
            KeptBench.release(bar);
            bench.passes();
            String differs = againstNew(bench, page);
            Object[] b = {KeptBench.held(bar), bench.stagedFare(), bench.slider() == bar, differs};
            // (c) several kept months, then a value set outside any draw: the reading of the latest draw follows.
            for (int i = 0; i < 4; i++) {
                month(g);
                bench.showInfrastructure();
                bench.passes();
            }
            javafx.scene.control.Slider later = bench.slider();
            double other = thumb - 2 * step >= bar.getMin() - 1e-9 ? thumb - 2 * step : thumb + 2 * step;
            later.setValue(other);
            String set = KeptBench.readingOf(later).getText();
            bench.stageFare(other);
            bench.showFresh(page);
            String built = KeptBench.readingOf(bench.slider()).getText();
            bench.restoreKeptPage();
            Object[] c = {later == bar, set, built, KeptBench.readingOf(later).getText()};
            bench.unstageFare();
            bench.showInfrastructure();
            bench.passes();
            return new Object[]{a, b, c, bench.stagedFare()};
        });
        if (fare == null) {
            check("the Transit page has a fare dial (a slider) to hold", false);
        } else {
            Object[] a = (Object[]) fare[0], b = (Object[]) fare[1], c = (Object[]) fare[2];
            System.out.printf("  dragged two steps: reading \"%s\" -> \"%s\"; a month later \"%s\" (a dial built at the thumb: \"%s\"), the ladder shows %.4f for a thumb at %.4f%n",
                    a[6], a[5], a[4], a[10], (double) a[7], (double) a[3]);
            check("a slider pressed is held", (boolean) a[0]);
            check("a month landing on a held slider leaves the same slider node, still held", (boolean) a[1] && (boolean) a[8]);
            check("...its value still the thumb's (" + a[2] + ")", Math.abs((double) a[2] - (double) a[3]) < 1e-9);
            check("...the reading the thumb's words in the month's money (\"" + a[4] + "\", not the staged \"" + a[6] + "\")",
                    a[4].equals(a[10]) && !a[4].equals(a[6]));
            check("...Ladder.shown() the thumb's value, not the staged one", Math.abs((double) a[7] - (double) a[3]) < 1e-9);
            check("...and nothing is staged before the release", Double.isNaN((double) a[9]));
            check("a slider released is not held", !(boolean) b[0]);
            check("...the fare is staged at the thumb (" + b[1] + ")", Math.abs((double) b[1] - (double) a[3]) < 1e-9);
            check("...and the page is the same slider, kept, equal to a page built new", (boolean) b[2] && b[3] == null);
            if (b[3] != null) System.out.println("  " + b[3]);
            check("after several kept months the one slider node still carries the dial", (boolean) c[0]);
            check("a value set outside any draw updates the reading of the latest draw (\"" + c[1] + "\", a dial built at it says \""
                    + c[2] + "\")", c[1].equals(c[2]));
            check("...and it is the reading on the page", c[3].equals(c[1]));
            check("the fare is unstaged at the end of the section", Double.isNaN((double) fare[3]));
        }

        // The filter box: City History's, through its real show method and the focus it takes back.
        String how = fx(() -> {
            bench.historyPreset(0);
            bench.showHistory();
            bench.passes();
            return bench.historyFilterFocus("rate");
        });
        System.out.println("  the filter box took the focus by: " + how);
        check("City History's filter box takes the focus (" + how + ")", how.equals("requestFocus") || how.equals("setFocused by reflection"));
        Object[] box = fx(() -> {
            Object[] was = bench.historyFilterState();
            month(g);
            bench.showHistory();
            bench.passes();
            Object[] now = bench.historyFilterState();
            return new Object[]{was, now};
        });
        Object[] was = (Object[]) box[0], now = (Object[]) box[1];
        check("a month landing keeps the filter box (the same node)", was != null && now != null && was[0] == now[0]);
        check("...its text (\"" + (now == null ? null : now[1]) + "\")", now != null && "rate".equals(now[1]));
        check("...its caret (" + (was == null ? null : was[2]) + " before, " + (now == null ? null : now[2]) + " after)",
                now != null && was != null && was[2].equals(now[2]));
        check("...and its focus", now != null && (boolean) now[3]);
        Object[] set = fx(() -> {
            // Kept.text on a field that has the focus leaves it alone, and sets one that has none.
            String focused = bench.historyFilterSet("zzz");
            bench.historyFilterBlur();
            String blurred = bench.historyFilterSet("zzz");
            bench.historyFilterSet("");
            bench.showHistory();
            bench.passes();
            return new Object[]{focused, blurred};
        });
        check("Kept.text leaves a field with the focus as the player typed it (\"" + set[0] + "\")", "rate".equals(set[0]));
        check("...and sets a field that has none (\"" + set[1] + "\")", "zzz".equals(set[1]));
    }

    /** Section 35: the hover kept. A node under a still pointer stays the same node in the look its own mouse-entered gives it, and in the rest look once the pointer is off. */
    static void hover(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 35: the hover is kept ---");
        java.util.Map<String, Boolean> found = new java.util.LinkedHashMap<>();
        // City History: a row that moves the chart (the hard times open), and a chart card's head (a line pinned).
        fx(() -> {
            bench.historyPreset(0);
            bench.historyFold("hard times", true);
            bench.historyPinLeft(TICKED);
            bench.drawHistoryKept();
            bench.passes();
            return null;
        });
        for (String kind : List.of("rowGoes", "chartCardHead", "door")) {
            String d = fx(() -> bench.hoverKept(kind, bench::drawHistoryKept));
            hoverLine(found, "City History", kind, d);
        }
        fx(() -> {
            bench.historyFold("hard times", false);
            return null;
        });
        // The three pages' own.
        KeptBench.Page infra = fx(bench::infrastructure);
        fx(() -> {
            bench.home(infra);
            bench.drawKept(infra);
            bench.passes();
            return null;
        });
        for (String kind : List.of("limitCell", "door")) {
            String d = fx(() -> bench.hoverKept(kind, () -> bench.drawKept(infra)));
            hoverLine(found, "Infrastructure", kind, d);
        }
        KeptBench.Page services = fx(bench::services);
        String doorCard = null;
        for (String[] sub : KeptBench.servicesPages()) {
            String d = fx(() -> {
                bench.home(services);
                bench.servicesOpen(sub[0], sub[1]);
                bench.drawKept(services);
                bench.passes();
                return bench.hoverKept("doorCard", () -> bench.drawKept(services));
            });
            if (!KeptBench.ABSENT.equals(d)) {
                hoverLine(found, "Services, " + sub[0] + ", " + sub[1], "doorCard", d);
                doorCard = d == null ? "ok" : d;
                break;
            }
        }
        if (doorCard == null) hoverLine(found, "Services", "doorCard", KeptBench.ABSENT);
        KeptBench.Page land = fx(bench::land);
        fx(() -> {
            bench.home(land);
            bench.drawKept(land);
            bench.passes();
            return null;
        });
        for (String kind : List.of("landRow", "oreCard")) {
            String d = fx(() -> bench.hoverKept(kind, () -> bench.drawKept(land)));
            hoverLine(found, "the land office", kind, d);
        }
        for (String kind : List.of("rowGoes", "door", "limitCell", "chartCardHead", "landRow", "oreCard", "doorCard")) {
            check("a " + kind + " was found on a page of this city, to put the pointer on", found.getOrDefault(kind, false));
        }
    }

    /** One hover result: printed, counted as found when the page had the node, and a check that it held. */
    private static void hoverLine(java.util.Map<String, Boolean> found, String where, String kind, String result) {
        if (KeptBench.ABSENT.equals(result)) {
            System.out.println("  " + where + " has no " + kind + " this month: not checked there");
            return;
        }
        found.put(kind, true);
        if (result != null) System.out.println("  " + where + ", " + kind + ": " + result);
        check(where + ": the " + kind + " under the pointer keeps its look across a kept redraw, and loses it when the pointer goes", result == null);
    }

    /* =====================================================================
       BUILD AND THE CONSTRUCTION PAGE (KP3), sections 36 to 46: each page kept, shown as a page built new shows it,
       kept right through what reshapes it, and cheap; then the stopped pages, the controls and the hover.
       ===================================================================== */

    /**
     * The budget for the kept Build month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, on the Overview and on a
     * city category with two orders dialled up and a card's stats pinned (the heavier of the two is the figure): twice the kept month's median,
     * rounded up (3.8 to 6.3 ms in five runs on the Overview, 4.3 between them; 3.7 to 9.0 ms on the category, 6.0 between them; the same
     * months built new 11.6 to 23.7 and 15.0 to 30.0 ms).
     */
    static final double KEPT_BUILD_MS = 12;

    /**
     * The budget for the kept construction page's month - its draw, then the pulse's CSS and layout, on the Sites tab with the city's
     * sites on - at CITY_MONTHS, on the PC: twice the kept month's median, rounded up (1.4 to 1.9 ms in five runs, 1.7 between them; the
     * same month built new 8.5 to 16.5 ms).
     */
    static final double KEPT_CONSTRUCTION_MS = 4;

    /** The rows the Sites tab and the timeline are kept at while Build and the construction page are watched: a site each. */
    static final int SITE_ROWS = 6;

    /** What is ordered, in turn, to put a site on: the cheap, quick works first. */
    static final String[] SITE_ORDER = {"Gravel Road", "Walk-in Clinic", "Paved Road", "Bus Network", "Elementary School",
            "Police Station", "Small Childcare Centre", "Water Treatment Plant", "General Hospital", "Crematorium"};

    private static int siteTurn;

    /** Ground bought through the model for an order that found none: the cheapest offer that covers the shortfall, the cash it lacks borrowed on Build's bond; whether it was bought. */
    static boolean buyGround(Game g, BuildingsTemplate t, int n) throws Exception {
        double lacking = Math.max(1, g.landNeededFor(t, n) - g.getLandManager().getAvailableSqFt());
        LandParcel p = g.bestOffer(Game.LandNeed.shortfall(lacking));
        if (p == null) return false;
        List<Integer> ids = List.of(p.getId());
        if (g.landNeedsFunding(ids)) {
            final double gap = g.landCashGap(ids);
            quietly(() -> g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
            if (g.landNeedsFunding(ids)) return false;
        }
        return quietly(() -> g.buyLandParcels(ids)) == 1;
    }

    /** Orders through the model until the city has `rows` sites on (a row each on the Sites tab, a bar each on the timeline): what it did, in a line. */
    static String ensureSites(Game g, int rows) throws Exception {
        BuildingManager bm = g.getBuildingManager();
        StringBuilder said = new StringBuilder();
        double borrowed = 0;
        for (int tries = 0; tries < SITE_ORDER.length * 2 && bm.siteKeysOnSite().size() < rows; tries++) {
            String name = SITE_ORDER[siteTurn++ % SITE_ORDER.length];
            BuildingsTemplate t = bm.getTemplateByName(name);
            if (t == null) continue;
            for (int q = 24; q >= 1; q /= 2) {
                final int n = q;
                Game.BuildResult r = quietly(() -> g.buildStack(t, n, false));
                // Short of cash, the city borrows the gap on Build's 20-year bond, as the funding page's offer does.
                if (r == Game.BuildResult.NEEDS_FUNDING) {
                    final double gap = g.buildFundingGap(t, n);
                    quietly(() -> g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
                    borrowed += gap;
                    r = quietly(() -> g.buildStack(t, n, false));
                }
                // ...and with no ground for even one, buys the cheapest parcel that covers the shortfall, as the refusal page's Buy does.
                if (r == Game.BuildResult.NO_LAND && n == 1 && buyGround(g, t, 1)) {
                    r = quietly(() -> g.buildStack(t, n, false));
                }
                if (r == Game.BuildResult.SUCCESS) {
                    said.append(said.length() > 0 ? ", " : "").append(n).append(" x ").append(name);
                    break;
                }
            }
        }
        return bm.siteKeysOnSite().size() + " sites on" + (said.length() > 0 ? " (ordered through the model: " + said + (borrowed > 0 ? "; the city borrowed " + String.format("%,.0f", borrowed) + " on Build's bond for them" : "") + ")" : "");
    }

    /** From now every month played tops the sites up to SITE_ROWS (the fixture holds while the pages are watched), until stopSites(). */
    static void keepSites(Game g) {
        afterMonth = () -> {
            try {
                ensureSites(g, SITE_ROWS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static void stopSites() {
        afterMonth = null;
    }

    /** Build's pages: the Overview and a city category with two orders dialled up (so that its order bar shows), kept, as built new, reshaped and costed. Sections 36 to 39. */
    static void build(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== Build (KP3) ===");
        System.out.println("  " + fx(() -> ensureSites(g, SITE_ROWS)) + "; cash " + String.format("%,.0f", g.getCash()));
        int onSite = fx(bench::sitesOn);
        check("the city has " + SITE_ROWS + " sites on to draw (" + onSite + ")", onSite >= SITE_ROWS);
        keepSites(g);
        final String cat = BuildAdvice.HEALTHCARE;
        KeptBench.Page probe = fx(() -> bench.build("probe", cat, java.util.Map.of()));
        List<String> names = fx(() -> {
            bench.home(probe);
            bench.drawKept(probe);
            bench.passes();
            return bench.buildCardNames();
        });
        System.out.println("  " + cat + " lays out " + names.size() + " cards, the first two " + names.subList(0, Math.min(2, names.size())));
        check("a city category has cards to dial an order on (" + names.size() + ")", names.size() >= 2);
        if (names.size() < 2) return;
        final String first = names.get(0), second = names.get(1);
        KeptBench.Page overview = fx(() -> bench.build("the Build Overview", BuildAdvice.OVERVIEW, java.util.Map.of()));
        KeptBench.Page ordered = fx(() -> bench.build("Build, " + cat + ", orders dialled up, a card pinned", cat, java.util.Map.of(first, 3, second, 2), java.util.Set.of(first)));
        String words = "3 × " + first + " + 2 × " + second;
        boolean bar = fx(() -> {
            bench.home(ordered);
            bench.drawKept(ordered);
            bench.passes();
            return bench.buildBarShows(words);
        });
        check("the order bar shows the order dialled up (\"" + words + "\")", bar);
        int fills = fx(() -> {
            bench.drawKept(ordered);
            int before = bench.buildBarFills();
            bench.drawKept(ordered);
            return bench.buildBarFills() - before;
        });
        check("a kept draw fills the order bar once, after the last card (" + fills + " fills)", fills == 1);

        pageKept(g, bench, overview, 36);
        pageKept(g, bench, ordered, 36);
        pageShows(g, bench, overview, 37);
        pageShows(g, bench, ordered, 37);
        // ...and a market category in its groups, two orders dialled up, the first card's stats pinned open.
        String market = null;
        for (String name : KeptBench.buildPages()) if (!name.equals(BuildAdvice.OVERVIEW) && !KeptBench.buildIsCity(name)) { market = name; break; }
        final String marketCat = market;
        KeptBench.Page marketProbe = fx(() -> bench.build("probe", marketCat, java.util.Map.of()));
        List<String> marketNames = fx(() -> {
            bench.home(marketProbe);
            bench.drawKept(marketProbe);
            bench.passes();
            return bench.buildCardNames();
        });
        check("a market category has cards to dial an order on (" + marketCat + ": " + marketNames.size() + ")", marketNames.size() >= 2);
        if (marketNames.size() >= 2) {
            KeptBench.Page marketPage = fx(() -> bench.build("Build, " + marketCat + ", orders dialled up, a card pinned", marketCat,
                    java.util.Map.of(marketNames.get(0), 4, marketNames.get(1), 1), java.util.Set.of(marketNames.get(0))));
            fx(() -> {
                bench.home(marketPage);
                bench.drawKept(marketPage);
                bench.passes();
                return null;
            });
            pageShows(g, bench, marketPage, 37);
        }

        // The Build screen, wherever it is: every choice that reshapes it.
        KeptBench.Page page = fx(() -> bench.build("the Build screen", BuildAdvice.OVERVIEW, java.util.Map.of()));
        List<Choice> choices = new ArrayList<>();
        for (String name : KeptBench.buildPages()) {
            choices.add(choice("the page " + name + " opened", b -> { b.buildOpen(name); return null; }));
        }
        // ...every ring of the two city categories with the most of them.
        List<String> cities = new ArrayList<>();
        for (String name : KeptBench.buildPages()) if (KeptBench.buildIsCity(name)) cities.add(name);
        cities.sort((a, b) -> Integer.compare(KeptBench.buildRings(b).size(), KeptBench.buildRings(a).size()));
        for (String city : cities.subList(0, 2)) {
            List<String> rings = KeptBench.buildRings(city);
            for (int i = 0; i < rings.size(); i++) {
                final int ring = i;
                choices.add(choice("the ring " + rings.get(i) + " picked on " + city, b -> {
                    b.buildOpen(city);
                    b.buildRing(city, ring);
                    return null;
                }));
            }
        }
        // ...an order dialled with a card's own stepper and cleared with its own, on a city category and on a market one.
        for (String at : market == null ? List.of(cat) : List.of(cat, market)) {
            choices.add(choice("an order dialled up on " + at + " (+10 and +, two cards)", b -> {
                b.buildOpen(at);
                b.drawKept(page);
                b.passes();
                return b.buildStep(0, "+10") && b.buildStep(1, "+") ? null : "the page has no two cards to dial";
            }));
            choices.add(choice("...a card's order cleared with its own ↺", b -> {
                b.buildOpen(at);
                b.drawKept(page);
                b.passes();
                return b.buildStep(0, "↺") ? null : "the page has no card to clear";
            }));
            choices.add(choice("...and the other cleared", b -> {
                b.buildOpen(at);
                b.drawKept(page);
                b.passes();
                return b.buildStep(1, "↺") ? null : "the page has no second card to clear";
            }));
            // ...a card's stats pinned open and let go.
            choices.add(choice("a card's stats pinned on " + at, b -> {
                b.buildOpen(at);
                b.drawKept(page);
                b.passes();
                return b.buildPin(0) ? null : "the page has no card to pin";
            }));
            choices.add(choice("...and unpinned", b -> {
                b.buildOpen(at);
                b.drawKept(page);
                b.passes();
                return b.buildPin(0) ? null : "the page has no card to unpin";
            }));
        }
        // ...automatic building on and off, on the Overview (its cards) and on a category (the head's chip to them).
        boolean autoWas = fx(() -> bench.buildAuto());
        choices.add(acting("automatic building turned on (the Overview)", b -> { b.buildOpen(BuildAdvice.OVERVIEW); b.buildAuto(true); return null; }));
        choices.add(choice("...and off", b -> { b.buildOpen(BuildAdvice.OVERVIEW); b.buildAuto(false); return null; }));
        choices.add(acting("automatic building turned on (" + cat + ": the head's chip)", b -> { b.buildOpen(cat); b.buildAuto(true); return null; }));
        choices.add(choice("...and off again", b -> { b.buildOpen(cat); b.buildAuto(false); return null; }));
        pageReshaped(g, bench, page, 38, choices);
        fx(() -> {
            bench.buildAuto(autoWas);
            bench.home(page);
            return null;
        });

        pageCost(g, bench, overview, 39, KEPT_BUILD_MS, "KEPT_BUILD_MS");
        pageCost(g, bench, ordered, 39, KEPT_BUILD_MS, "KEPT_BUILD_MS");
        int heldBuild = fx(bench::sitesOn);
        check("the sites were held through the months: " + heldBuild + " on at the end (SITE_ROWS " + SITE_ROWS + ")", heldBuild >= SITE_ROWS);
    }

    /** The construction page, with the city's sites on: the Sites tab kept, shown, reshaped by its tabs, a pick and the find box, and costed. Sections 40 to 43. */
    static void construction(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The construction page (KP3) ===");
        System.out.println("  " + fx(() -> ensureSites(g, SITE_ROWS)));
        int rows = fx(bench::sitesOn);
        check("the city has " + SITE_ROWS + " sites on to draw (" + rows + ")", rows >= SITE_ROWS);
        keepSites(g);
        KeptBench.Page page = fx(bench::construction);
        pageKept(g, bench, page, 40);
        pageShows(g, bench, page, 41);
        String city = fx(() -> bench.constructionPickable(false));
        String theirs = fx(() -> bench.constructionPickable(true));
        System.out.println("  to pick on Demolish: " + city + " (the city's), " + theirs + " (a buy-out)");
        // The other two tabs as pictures too: the Timeline (its bars) and Demolish with two of a building picked (its staging card).
        KeptBench.Page timeline = fx(() -> bench.construction("Timeline"));
        fx(() -> {
            bench.home(timeline);
            bench.drawKept(timeline);
            bench.passes();
            return null;
        });
        pageShows(g, bench, timeline, 41);
        KeptBench.Page demolish = fx(() -> bench.construction("Demolish"));
        fx(() -> {
            bench.home(demolish);
            if (city != null) bench.constructionPick(city, false, 2);
            bench.drawKept(demolish);
            bench.passes();
            return null;
        });
        pageShows(g, bench, demolish, 41);
        fx(() -> {
            bench.home(page);
            return null;
        });
        List<Choice> choices = new ArrayList<>();
        choices.add(choice("the Timeline tab", b -> { b.constructionTab("Timeline"); return null; }));
        choices.add(choice("the Demolish tab", b -> { b.constructionTab("Demolish"); return null; }));
        if (city != null) {
            choices.add(choice("a building of the city's picked to demolish (" + city + ")", b -> { b.constructionPick(city, false, 1); return null; }));
            choices.add(choice("...two of it", b -> { b.constructionPick(city, false, 2); return null; }));
        }
        if (theirs != null) {
            choices.add(choice("a business's building picked to buy out (" + theirs + ")", b -> { b.constructionPick(theirs, false, 1); return null; }));
        }
        choices.add(choice("nothing picked again", b -> { b.constructionPick(null, false, 1); return null; }));
        choices.add(choice("a word typed in the find box (road)", b -> {
            b.drawKept(page);
            b.passes();
            return b.fieldState() != null ? (b.fieldType("road").equals("road") ? null : "the box did not take the word") : "the Demolish tab has no find box";
        }));
        choices.add(choice("a word that matches nothing typed (zzz)", b -> {
            b.drawKept(page);
            b.passes();
            return b.fieldState() != null ? (b.fieldType("zzz").equals("zzz") ? null : "the box did not take the word") : "the Demolish tab has no find box";
        }));
        choices.add(choice("the find box emptied", b -> {
            b.drawKept(page);
            b.passes();
            return b.fieldState() != null ? (b.fieldType("").isEmpty() ? null : "the box did not empty") : "the Demolish tab has no find box";
        }));
        choices.add(choice("the Sites tab again", b -> { b.constructionTab("Sites"); return null; }));
        pageReshaped(g, bench, page, 42, choices);
        pageCost(g, bench, page, 43, KEPT_CONSTRUCTION_MS, "KEPT_CONSTRUCTION_MS");
        int heldRows = fx(bench::sitesOn);
        check("the sites were held through the months: " + heldRows + " on at the end (SITE_ROWS " + SITE_ROWS + ")", heldRows >= SITE_ROWS);
        stopSites();
    }

    /**
     * Section 44: the pages a build is stopped on and the run's funding and fell-short pages, each caused where the model can
     * cause it (a count Game.buildRunStop() refuses for the reason; a run larger than the cash) and each drawn as an arrival,
     * then a month later again, equal to a page built new. The line says which were caused and which were drawn for a building
     * that merely names the page.
     */
    static void stopped(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 44: the pages a build is stopped on, and the funding and fell-short pages ---");
        List<KeptBench.Page> pages = new ArrayList<>();
        StringBuilder caused = new StringBuilder(), named = new StringBuilder();
        for (String kind : KeptBench.STOPPED) {
            Object[] found = fx(() -> bench.stoppedFor(kind));
            check("a building names the no-" + kind + " page", found != null);
            if (found == null) continue;
            BuildingsTemplate t = (BuildingsTemplate) found[0];
            int q = (int) found[1];
            StringBuilder into = ((boolean) found[2]) ? caused : named;
            into.append(into.length() > 0 ? "; " : "").append(kind).append(": ").append(q).append(" x ").append(t.getName());
            pages.add(fx(() -> bench.buildStopped(kind, t, q)));
        }
        System.out.println("  caused (the model refuses the order for it): " + (caused.length() == 0 ? "none" : caused));
        System.out.println("  drawn for a building that merely names the page (the city meets no such refusal): " + (named.length() == 0 ? "none" : named));
        Object[] run = fx(bench::fundingRun);
        if (run == null) {
            System.out.println("  the city can pay for every order it could place: no funding page to draw");
        } else {
            BuildingsTemplate t = (BuildingsTemplate) run[0];
            int q = (int) run[1];
            System.out.println("  funding page caused: " + q + " x " + t.getName() + " costs more than the cash (" + String.format("%,.0f", g.getCash()) + ")");
            pages.add(fx(() -> bench.buildFunding(t, q)));
            // The fell-short page is never reached by the model (the loan's invoice is the run's): drawn for these orders, as the model words them.
            BuildingsTemplate road = g.getBuildingManager().getTemplateByName("Gravel Road");
            System.out.println("  fell-short pages: drawn for the same run, which the model would not leave unbuilt (no way to cause it)");
            pages.add(fx(() -> bench.buildFellShort(road, 2, t, q, "20-year bond")));
            pages.add(fx(() -> bench.buildFellShort(null, 0, t, q, "20-year bond")));
        }
        for (KeptBench.Page p : pages) {
            pageReshaped(g, bench, p, 44, List.of(choice(p.name() + " opened", b -> null)));
        }
    }

    /**
     * Section 45: a control keeps the player's hand. Automatic building's dial (the first slider on the Overview, through the
     * screen's real show method): pressed it is held, a month leaves its node, thumb and reading; released, the margin is set at
     * the thumb and the page equals one built new. The auto cap chips' yield box, with the focus, keeps its node, text, caret and
     * focus across a month; so does the construction page's find box. A stepper pressed between draws refills the order bar in
     * place and the next kept draw equals a page built new.
     */
    static void buildControls(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 45: a control keeps the player's hand ---");
        AutoBuilder auto = g.getAutoBuilder();
        double slack = auto.getSlack();
        Auction.Cap cap = auto.getCap();
        KeptBench.Page overview = fx(() -> bench.build("the Build Overview", BuildAdvice.OVERVIEW, java.util.Map.of()));
        Object[] dial = fx(() -> {
            bench.home(overview);
            bench.showBuild();
            bench.passes();
            javafx.scene.control.Slider bar = bench.slider();
            if (bar == null) return null;
            double step = bar.getBlockIncrement();
            double snapped = bar.getMin() + Math.round((bar.getValue() - bar.getMin()) / step) * step;
            double thumb = snapped + 2 * step <= bar.getMax() + 1e-9 ? snapped + 2 * step : snapped - 2 * step;
            String before = KeptBench.readingOf(bar).getText();
            // (a) pressed, dragged two steps, a month lands.
            KeptBench.press(bar);
            boolean heldPressed = KeptBench.held(bar);
            bar.setValueChanging(true);
            bar.setValue(thumb);
            String dragged = KeptBench.readingOf(bar).getText();
            month(g);
            bench.showBuild();
            bench.passes();
            boolean sameBar = bench.slider() == bar;
            String kept = KeptBench.readingOf(bar).getText();
            double shown = KeptBench.shownBy(bar);
            boolean heldStill = KeptBench.held(bar);
            double valueNow = bar.getValue();
            double slackMid = auto.getSlack();
            Object[] a = {heldPressed, sameBar, valueNow, thumb, kept, dragged, before, shown, heldStill, slackMid, String.format("%.0f%%", thumb)};
            // (b) released: the margin is set at the thumb, the screen drawn again, the page the page built new.
            bar.setValueChanging(false);
            KeptBench.release(bar);
            bench.passes();
            double slackSet = auto.getSlack();
            boolean heldAfter = KeptBench.held(bar);
            boolean same = bench.slider() == bar;
            String differs = againstNew(bench, overview);
            Object[] b = {heldAfter, slackSet, same, differs};
            // (c) several kept months, then a value set outside any draw: the reading of the latest draw follows.
            for (int i = 0; i < 4; i++) {
                month(g);
                bench.showBuild();
                bench.passes();
            }
            javafx.scene.control.Slider later = bench.slider();
            double other = thumb - 2 * step >= bar.getMin() - 1e-9 ? thumb - 2 * step : thumb + 2 * step;
            later.setValue(other);
            Object[] c = {later == bar, KeptBench.readingOf(later).getText(), String.format("%.0f%%", other)};
            auto.setSlack(slack, g.getDecisions());
            bench.showBuild();
            bench.passes();
            return new Object[]{a, b, c, auto.getSlack()};
        });
        if (dial == null) {
            check("the Overview has a dial (a slider) to hold", false);
        } else {
            Object[] a = (Object[]) dial[0], b = (Object[]) dial[1], c = (Object[]) dial[2];
            System.out.printf("  the margin dragged two steps: reading \"%s\" -> \"%s\"; a month later \"%s\", the ladder shows %.1f for a thumb at %.1f%n",
                    a[6], a[5], a[4], (double) a[7], (double) a[3]);
            check("a slider pressed is held", (boolean) a[0]);
            check("a month landing on a held slider leaves the same slider node, still held", (boolean) a[1] && (boolean) a[8]);
            check("...its value still the thumb's (" + a[2] + ")", Math.abs((double) a[2] - (double) a[3]) < 1e-9);
            check("...the reading the thumb's words (\"" + a[4] + "\", not the margin's \"" + a[6] + "\")", a[4].equals(a[10]) && !a[4].equals(a[6]));
            check("...Ladder.shown() the thumb's value", Math.abs((double) a[7] - (double) a[3]) < 1e-9);
            check("...and the margin is not set before the release (" + String.format("%.2f", (double) a[9]) + ")", Math.abs((double) a[9] - slack) < 1e-9);
            check("a slider released is not held", !(boolean) b[0]);
            check("...the margin is set at the thumb (" + String.format("%.2f", (double) b[1]) + ")", Math.abs((double) b[1] * 100 - (double) a[3]) < 1e-6);
            check("...and the page is the same slider, kept, equal to a page built new", (boolean) b[2] && b[3] == null);
            if (b[3] != null) System.out.println("  " + b[3]);
            check("after several kept months the one slider node still carries the dial", (boolean) c[0]);
            check("a value set outside any draw updates the reading of the latest draw (\"" + c[1] + "\", a thumb at it reads \"" + c[2] + "\")", c[1].equals(c[2]));
            check("the margin is put back at the end of the section", Math.abs((double) dial[3] - slack) < 1e-9);
        }

        // The auto cap chips' yield box, with the focus, through the real show method.
        String how = fx(() -> {
            bench.home(overview);
            bench.showBuild();
            bench.passes();
            return bench.fieldState() == null ? "the Overview has no text field" : bench.fieldFocus("4.5");
        });
        System.out.println("  the yield box took the focus by: " + how);
        check("the Overview's yield box takes the focus (" + how + ")", how.equals("requestFocus") || how.equals("setFocused by reflection"));
        Object[] box = fx(() -> {
            Object[] was = bench.fieldState();
            boolean yieldBox = was != null && ((javafx.scene.control.TextField) was[0]).getPromptText().startsWith("type a yield");
            month(g);
            bench.showBuild();
            bench.passes();
            Object[] now = bench.fieldState();
            String focused = bench.fieldSet("zzz");
            // Cleared and let go with nothing in it, so that leaving it sets no cap.
            bench.fieldType("");
            bench.fieldBlur();
            String blurred = bench.fieldSet("zzz");
            bench.fieldSet("");
            bench.showBuild();
            bench.passes();
            return new Object[]{was, now, yieldBox, focused, blurred};
        });
        Object[] was = (Object[]) box[0], now = (Object[]) box[1];
        check("the box found is the cap chips' yield box", (boolean) box[2]);
        check("a month landing keeps the yield box (the same node)", was != null && now != null && was[0] == now[0]);
        check("...its text (\"" + (now == null ? null : now[1]) + "\")", now != null && "4.5".equals(now[1]));
        check("...its caret (" + (was == null ? null : was[2]) + " before, " + (now == null ? null : now[2]) + " after)", now != null && was != null && was[2].equals(now[2]));
        check("...and its focus", now != null && (boolean) now[3]);
        check("Kept.text leaves a yield box with the focus as the player typed it (\"" + box[3] + "\")", "4.5".equals(box[3]));
        check("...and sets one that has none (\"" + box[4] + "\")", "zzz".equals(box[4]));
        check("the builder's cap is as it was (nothing typed was set)", auto.getCap().equals(cap));

        // The construction page's find box.
        KeptBench.Page sites = fx(bench::construction);
        String findHow = fx(() -> {
            bench.home(sites);
            bench.constructionTab("Demolish");
            bench.showConstruction();
            bench.passes();
            return bench.fieldState() == null ? "the Demolish tab has no text field" : bench.fieldFocus("road");
        });
        System.out.println("  the find box took the focus by: " + findHow);
        check("the construction page's find box takes the focus (" + findHow + ")", findHow.equals("requestFocus") || findHow.equals("setFocused by reflection"));
        Object[] find = fx(() -> {
            Object[] wasBox = bench.fieldState();
            boolean findBox = wasBox != null && ((javafx.scene.control.TextField) wasBox[0]).getPromptText().startsWith("Find a building");
            month(g);
            bench.showConstruction();
            bench.passes();
            Object[] nowBox = bench.fieldState();
            String focused = bench.fieldSet("zzz");
            // The screen's own filter changed under the player's hand (not typed): the draw leaves the focused box as it is typed, and sets it once the focus is gone.
            bench.constructionFilter("changed under the hand");
            bench.showConstruction();
            bench.passes();
            Object[] underHand = bench.fieldState();
            bench.fieldBlur();
            bench.showConstruction();
            bench.passes();
            Object[] letGo = bench.fieldState();
            String blurred = bench.fieldSet("zzz");
            bench.fieldSet("");
            bench.showConstruction();
            bench.passes();
            return new Object[]{wasBox, nowBox, findBox, focused, blurred, underHand, letGo};
        });
        Object[] wasF = (Object[]) find[0], nowF = (Object[]) find[1];
        check("the box found is the find box", (boolean) find[2]);
        check("a month landing keeps the find box (the same node)", wasF != null && nowF != null && wasF[0] == nowF[0]);
        check("...its text (\"" + (nowF == null ? null : nowF[1]) + "\")", nowF != null && "road".equals(nowF[1]));
        check("...its caret (" + (wasF == null ? null : wasF[2]) + " before, " + (nowF == null ? null : nowF[2]) + " after)", nowF != null && wasF != null && wasF[2].equals(nowF[2]));
        check("...and its focus", nowF != null && (boolean) nowF[3]);
        check("Kept.text leaves a find box with the focus as the player typed it (\"" + find[3] + "\")", "road".equals(find[3]));
        check("...and sets one that has none (\"" + find[4] + "\")", "zzz".equals(find[4]));
        Object[] underHand = (Object[]) find[5], letGo = (Object[]) find[6];
        check("the screen's own filter changed under the hand leaves the focused find box as typed (\"" + (underHand == null ? null : underHand[1]) + "\")",
                underHand != null && wasF != null && "road".equals(underHand[1]) && underHand[0] == wasF[0]);
        check("...and the next draw, once the focus is gone, sets the box to it (\"" + (letGo == null ? null : letGo[1]) + "\")",
                letGo != null && wasF != null && "changed under the hand".equals(letGo[1]) && letGo[0] == wasF[0]);
        fx(() -> {
            bench.home(sites);
            return null;
        });

        // A stepper pressed between draws: the order bar refilled in place, the next kept draw a page built new.
        final String cat = BuildAdvice.HEALTHCARE;
        KeptBench.Page cards = fx(() -> bench.build("Build, " + cat, cat, java.util.Map.of()));
        Object[] press = fx(() -> {
            bench.home(cards);
            bench.drawKept(cards);
            bench.passes();
            List<String> names = bench.buildCardNames();
            String word = "1 × " + names.get(0);
            int fillsBefore = bench.buildBarFills();
            boolean pressed = bench.buildStep(0, "+");
            int fillsByPress = bench.buildBarFills() - fillsBefore;
            String count = bench.buildCount(0);
            boolean barBetween = bench.buildBarShows(word);
            bench.drawKept(cards);
            bench.passes();
            boolean barAfter = bench.buildBarShows(word);
            String after = againstNew(bench, cards);
            // ...and a hundred more, a month, and the draw.
            bench.drawKept(cards);
            boolean pressed100 = bench.buildStep(0, "+100");
            month(g);
            bench.drawKept(cards);
            String monthLater = againstNew(bench, cards);
            // ...cleared by the card's own ↺, outside a draw: the bar goes and the next draw is a page built new.
            bench.drawKept(cards);
            boolean cleared = bench.buildStep(0, "↺");
            boolean barGone = !bench.buildBarShows(word) && !bench.buildBarShows("101 × " + names.get(0));
            bench.drawKept(cards);
            String afterClear = againstNew(bench, cards);
            bench.home(cards);
            return new Object[]{pressed, count, barBetween, barAfter, after, pressed100, monthLater, cleared, barGone, afterClear, fillsByPress};
        });
        check("a card's + pressed between draws is a press on the card (the count reads \"" + press[1] + "\")", (boolean) press[0] && "1".equals(press[1]));
        check("...the order bar is refilled in place at once, outside any draw, once (" + press[10] + " fill)", (boolean) press[2] && (int) press[10] == 1);
        check("...and the next kept draw keeps the bar and equals a page built new", (boolean) press[3] && press[4] == null);
        if (press[4] != null) System.out.println("  " + press[4]);
        check("+100, a month and a kept draw equal a page built new", (boolean) press[5] && press[6] == null);
        if (press[6] != null) System.out.println("  " + press[6]);
        check("the card's own ↺ clears the order, the bar goes, and the next kept draw equals a page built new", (boolean) press[7] && (boolean) press[8] && press[9] == null);
        if (press[9] != null) System.out.println("  " + press[9]);
    }

    /** Section 46: the hover. A card's (i) keeps its stat cover shown across a kept draw while the pointer is over it, and hides it once the pointer goes; and the Overview's tiles, a category's door and a small ring card keep the look their own mouse-entered gives them. */
    static void buildHover(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 46: the hover is kept (Build) ---");
        java.util.Map<String, Boolean> found = new java.util.LinkedHashMap<>();
        KeptBench.Page overview = fx(() -> bench.build("the Build Overview", BuildAdvice.OVERVIEW, java.util.Map.of()));
        fx(() -> {
            bench.home(overview);
            bench.drawKept(overview);
            bench.passes();
            return null;
        });
        for (String kind : List.of("jobTile", "marketTile", "door")) {
            String d = fx(() -> bench.hoverKept(kind, () -> bench.drawKept(overview)));
            hoverLine(found, "the Build Overview", kind, d);
        }
        for (String city : List.of(BuildAdvice.HEALTHCARE, BuildAdvice.EDUCATION)) {
            KeptBench.Page page = fx(() -> bench.build("Build, " + city, city, java.util.Map.of()));
            fx(() -> {
                bench.home(page);
                bench.drawKept(page);
                bench.passes();
                return null;
            });
            for (String kind : city.equals(BuildAdvice.EDUCATION) ? List.of("door", "ringCard") : List.of("door")) {
                String d = fx(() -> bench.hoverKept(kind, () -> bench.drawKept(page)));
                hoverLine(found, "Build, " + city, kind, d);
            }
            String d = fx(() -> bench.hoverCover(() -> bench.drawKept(page)));
            hoverLine(found, "Build, " + city, "(i) cover", d);
        }
        for (String kind : List.of("jobTile", "marketTile", "door", "ringCard", "(i) cover")) {
            check("a " + kind + " was found on a Build page of this city, to put the pointer on", found.getOrDefault(kind, false));
        }
    }

    /* =====================================================================
       SECTORS (KP4), sections 47 to 54: the list and a business's five pages, each page drawn kept from the screen's pool and
       built new from an empty one - kept, shown as a page built new shows it, kept right through what reshapes it - then the
       pictures on them (the refinery's, a materials industry's, a leg's bar), the count-up the clock allows, what it costs, the
       oil reserve's dials, and the hover.
       ===================================================================== */

    /**
     * The budget for the kept Sectors list month - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC: twice the
     * kept month's median, rounded up (8.3 to 9.2 ms in five runs, 8.8 between them; the same month built new 23.8 to 26.0 ms).
     */
    static final double KEPT_SECTORS_MS = 18;

    /**
     * The budget for the kept month on a business's page - its draw, then the pulse's CSS and layout - at CITY_MONTHS, on the PC, the
     * heaviest of the three pages costed (the shops' Operations; a plain business's Income statement and wood products' Operations
     * weigh less): twice the kept month's median, rounded up (the shops' Operations 5.1 to 7.8 ms in five runs, 7.1 between them, the
     * same month built new 25.3 to 41.0 ms; the Income statement 2.7 to 3.9 ms, 2.9 between them, built new 10.1 to 11.5; wood products'
     * 5.4 to 9.3 ms, 5.5 between them, built new 19.0 to 25.1).
     */
    static final double KEPT_SECTOR_MS = 15;

    /** The months the pictures are watched through (section 50): each month a repaint or not, and a page built new beside it. */
    static final int PICTURE_MONTHS = 6;

    /** The businesses the Sectors sections take besides the shops, the landlords and Freight: a plain one with a plant standing, and a materials industry with works to draw. */
    record SectorKinds(String plain, String materials) { }

    /** The keys a plain business is not taken from: the shops, the kitchens, the counters, the landlords, the haulier and the fuel's two. */
    private static final java.util.Set<String> NOT_PLAIN = java.util.Set.of(Sectors.RETAIL, Sectors.RESTAURANTS, Sectors.LUXURY_RETAIL,
            Sectors.REAL_ESTATE, Sectors.FREIGHT, Sectors.OIL, Sectors.REFINING);

    /** The plain business this city has, by the model's own reading of its plants, and the materials industry its works draw: wood products, which ensureWorks() puts up a sawmill for (null when it has none standing). */
    static SectorKinds sectorKinds(Game g) {
        String plain = null;
        for (Sector s : g.getSectors().all()) {
            if (plain == null && !IndustryView.covers(s) && !NOT_PLAIN.contains(s.key())) {
                SectorFlow.Plant p = SectorFlow.plant(s);
                if (!p.none() && !Double.isFinite(p.let()) && p.rate() > 0) plain = s.key();
            }
        }
        boolean works = IndustryView.of(g, g.getSectors().byKey(Sectors.WOOD_PRODUCTS)).drawn();
        return new SectorKinds(plain, works ? Sectors.WOOD_PRODUCTS : null);
    }

    /** What a materials industry's works are made of, in turn, for a city that has none standing: a sawmill first, the rest if it cannot. */
    static final String[] WORKS_ORDER = {"Sawmill", "Scrap Yard", "Wood Pellet Mill", "Panel Mill"};

    /**
     * Wood products' works standing in the city: the ones it has, else a sawmill put up through the model (finished at once; the
     * city borrows what it lacks on Build's bond and buys ground where it finds none, as ensureSites() does), a month played so
     * that the industries' month is counted; what it found or did, in a line. Which materials industries the long-played city
     * has works for is its AutoBuilder's choice (a city of 150 months had wood works, one of 600 had none, one of 1,400 iron and
     * steel's), so the fixture takes the one it can build whatever the city did: the sawmill's.
     */
    static String ensureWorks(Game g) throws Exception {
        if (IndustryView.of(g, g.getSectors().byKey(Sectors.WOOD_PRODUCTS)).standing() > 0) return "the city has wood products works standing already";
        String said = putUpWorks(g);
        if (said.startsWith("a ")) month(g);
        return said.startsWith("a ") ? "the city had no materials works standing: " + said + ", a month played" : said;
    }

    /** A sawmill (or the first of WORKS_ORDER the model lets be built) put up finished: "a Sawmill put up through the model", or why none could be. */
    static String putUpWorks(Game g) throws Exception {
        BuildingManager bm = g.getBuildingManager();
        StringBuilder tried = new StringBuilder();
        for (String name : WORKS_ORDER) {
            BuildingsTemplate t = bm.getTemplateByName(name);
            if (t == null) continue;
            Game.BuildResult r = quietly(() -> g.buildStack(t, 1, true));
            if (r == Game.BuildResult.NEEDS_FUNDING) {
                final double gap = g.buildFundingGap(t, 1);
                quietly(() -> g.handleLongBondForCash(gap, Game.BUILD_BOND_YEARS, Game.BUILD_BOND_GRANULE));
                r = quietly(() -> g.buildStack(t, 1, true));
            }
            if (r == Game.BuildResult.NO_LAND && buyGround(g, t, 1)) r = quietly(() -> g.buildStack(t, 1, true));
            if (r == Game.BuildResult.SUCCESS) return "a " + name + " put up through the model";
            tried.append(tried.length() > 0 ? ", " : "").append(name).append(" ").append(r);
        }
        return "no materials works could be put up (" + tried + ")";
    }

    /** A page arrived at: back on its home, drawn and laid out, as the bench's other pages are before their sections. */
    static void arrive(KeptBench bench, KeptBench.Page page) throws Exception {
        fx(() -> {
            bench.home(page);
            bench.drawKept(page);
            bench.passes();
            return null;
        });
    }

    /** The hand that opens a business's page in a view. */
    static Hand on(String key, String page, boolean statement) {
        return b -> {
            b.sectorsOpen(key, page, statement);
            return null;
        };
    }

    /** What a business is called on the screen. */
    static String called(Game g, String key) {
        return g.getSectors().byKey(key).label();
    }

    /** A name's possessive as the lines say it: "Retail's", "Wood Products'". */
    static String possessive(String name) {
        return name.endsWith("s") ? name + "'" : name + "'s";
    }

    /* =====================================================================
       THE RESEARCH SCREENS ON THE SECTORS AND BUILD TABS (R5, slice B2): sections 96 and 97
       ===================================================================== */

    /** The months each of the research sections plays on its page, kept over last month's nodes against a page built new. */
    static final int R5_MONTHS = 6;

    /** How many times `part` is in `whole`. */
    static int occurrences(String whole, String part) {
        int n = 0;
        for (int at = whole.indexOf(part); at >= 0; at = whole.indexOf(part, at + part.length())) n++;
        return n;
    }

    /**
     * Sections 96 and 97: a sector's Investors page with its equipment, and the Build tab's NEW, each drawn kept over last month's nodes and built new in
     * a city founded the 1900 way (ResearchViewCheck.researchTown(): the fixture the research screens share), and in the played city, which has
     * nothing of either to draw.
     */
    static void researchScreens(Game g, KeptBench played) throws Exception {
        System.out.println("\n=== The research screens on Sectors and Build (R5, slice B2): a city founded the 1900 way ===");
        long t = System.nanoTime();
        int c = checks;
        researchEquipment(g, played);
        t = tookWith("the Investors page's equipment (96)", t, c);
        c = checks;
        researchBuild(g, played);
        tookWith("the build cards' NEW (97)", t, c);
    }

    /**
     * Section 96: a sector's Investors page with its equipment, kept. The sector of the fixture's unfitted stack has ITS EQUIPMENT on its page - the
     * piece's card with the stack's row, how many carry it and the model's verdict, in the words the model's figures give - and the page drawn kept over
     * last month's nodes is the page built new, month after month; the farms' page, which has none until the tractor is found here, then has a card with
     * where it came from, what it does and "its gain cannot be had yet", and comes and goes with the find as a page built new does; and a city founded
     * the default way has no ITS EQUIPMENT on any Investors page.
     */
    static void researchEquipment(Game g, KeptBench played) throws Exception {
        System.out.println("\n--- 96: a sector's Investors page with its equipment, kept ---");
        Game town = ResearchViewCheck.researchTown();
        KeptBench bench = fx(() -> new KeptBench(town));
        ResearchView.PieceRow stack = null;
        for (ResearchView.PieceRow r : ResearchView.equipment(town, null)) {
            if (r.unfitted() > 0) { stack = r; break; }
        }
        check("the town has a stack that lacks a piece the city knows (the page's premise)", stack != null);
        if (stack == null) return;
        final ResearchView.PieceRow row = stack;
        String key = row.template().getSector();
        KeptBench.Page page = fx(() -> bench.sectors(possessive(called(town, key)) + " Investors page", key, "Investors", false));
        arrive(bench, page);
        String shown = String.join("\n", fx(bench::signature));
        check("ITS EQUIPMENT is on " + page.name() + ", once", occurrences(shown, "\"ITS EQUIPMENT\"") == 1);
        check("...with the piece's card (" + row.piece().getName() + ") and the stack's row (" + row.template().getName() + ")",
                shown.contains("\"" + row.piece().getName() + "\"") && shown.contains("\"" + row.template().getName() + "\""));
        check("...saying how many carry it, as the model counts them (" + row.fitted() + " of " + row.buildings() + " fitted)",
                shown.contains(String.format("\"%,d of %,d fitted\"", row.fitted(), row.buildings())));
        String verdict = switch (row.verdict().kind()) {
            case FITTED -> "all fitted";
            case PAYS -> "next: it pays";
            case NOT_YET -> "not worth it yet";
            default -> "not known yet";
        };
        check("...and the model's verdict on the next one (" + row.verdict().kind() + ": \"" + verdict + "\")", shown.contains("\"" + verdict + "\""));
        check("...and where it came from (known since the founding, or from abroad)",
                shown.contains("\"known since the founding\"") || shown.contains("\"from abroad, "));

        // The page drawn kept over last month's nodes is the page built new, month after month.
        String first = null;
        double handed = 0;
        for (int m = 0; m < R5_MONTHS; m++) {
            Object[] r = fx(() -> {
                month(town);
                bench.drawKept(page);
                int[] counts = bench.counts(page);
                return new Object[]{againstNew(bench, page), counts[1] / (double) Math.max(1, counts[0] + counts[1])};
            });
            if (r[0] != null && first == null) first = "month " + town.getMonth() + ", " + r[0];
            handed += (double) r[1];
        }
        if (first != null) System.out.println("  first difference: " + first);
        check("the kept page's signature equals the new page's for " + R5_MONTHS + " months", first == null);
        System.out.printf("  %d months: %.1f%% of the page's nodes handed back a month on average%n", R5_MONTHS, handed / R5_MONTHS * 100);
        check("each month hands back nine in ten of the nodes, or more", handed / R5_MONTHS >= HANDED_BACK);
        // KP2b's rule (section 92's nothingSet()) on the page with ITS EQUIPMENT, which no default city has.
        check("the page still has equipment rows to draw (the next check's premise)", !ResearchView.equipment(town, key).isEmpty());
        nothingSet(bench, page, page.name() + " in the town, ITS EQUIPMENT on it");

        // The farms have no row until the tractor is found here; then they have a card with its origin and what it does.
        BuildingsTemplate farm = ResearchViewCheck.template(town, "Mixed Farm");
        String farmKey = farm.getSector();
        java.util.Map<String, Integer> without = new java.util.HashMap<>(town.getResearch().foundHere());
        without.remove("PE14");
        java.util.Map<String, Integer> with = new java.util.HashMap<>(without);
        with.put("PE14", 5);
        Hand lose = b -> {
            ResearchViewCheck.setFinds(town, without);
            return null;
        };
        Hand find = b -> {
            ResearchViewCheck.setFinds(town, with);
            return ResearchView.equipment(town, farmKey).isEmpty() ? "the tractor found gives the farms no row" : null;
        };
        KeptBench.Page farms = fx(() -> bench.sectors(possessive(called(town, farmKey)) + " Investors page", farmKey, "Investors", false));
        fx(() -> lose.on(bench));
        check("before the tractor is found the farms have no equipment to show (the model's rows)", ResearchView.equipment(town, farmKey).isEmpty());
        arrive(bench, farms);
        check("...and no ITS EQUIPMENT on their page", !String.join("\n", fx(bench::signature)).contains("\"ITS EQUIPMENT\""));
        fx(() -> find.on(bench));
        arrive(bench, farms);
        String farmsShown = String.join("\n", fx(bench::signature));
        List<ResearchView.PieceRow> farmRows = ResearchView.equipment(town, farmKey);
        ResearchView.PieceRow tractor = farmRows.isEmpty() ? null : farmRows.get(0);
        check("the tractor found here gives the farms a row in the model", tractor != null && tractor.from() != null
                && tractor.from().source() == ResearchView.Source.HERE);
        if (tractor != null && tractor.from() != null) {
            ResearchView.Discovery d = tractor.from();
            check("...ITS EQUIPMENT is on their page with the piece (" + tractor.piece().getName() + ")",
                    farmsShown.contains("\"ITS EQUIPMENT\"") && farmsShown.contains("\"" + tractor.piece().getName() + "\""));
            check("...where it came from, the month and the years ahead as the discovery says them", farmsShown.contains(String.format(java.util.Locale.ROOT,
                    "\"found here, %s · %.1f years before the world\"", CityCalendar.format(d.month()), d.yearsAhead())));
            check("...what it does, in the entry's words", d.entry().effect().isBlank() || farmsShown.contains(d.entry().effect()));
            boolean cannot = !Double.isFinite(tractor.verdict().earnsBack()) || !Double.isFinite(tractor.verdict().cost());
            check("...and, as the model prices it (" + tractor.verdict().kind() + (cannot ? ", no gain to be had" : "")
                            + "), what it earns back or that its gain cannot be had",
                    cannot ? farmsShown.contains("\"its gain cannot be had yet\"") : farmsShown.contains("earns back"));
        }
        fx(() -> lose.on(bench));
        pageReshaped(town, bench, farms, 96, List.of(
                new Choice("the tractor found here (the farms' ITS EQUIPMENT comes)", find, true),
                choice("...and lost again (it goes)", lose),
                new Choice("...and found again", find, true)));

        // A city founded the default way has none of it.
        check("a city founded the default way has no equipment rows in the model", ResearchView.equipment(g, null).isEmpty());
        KeptBench.Page shops = fx(() -> played.sectors(possessive(called(g, Sectors.RETAIL)) + " Investors page in the played city", Sectors.RETAIL,
                "Investors", false));
        arrive(played, shops);
        check("...and no ITS EQUIPMENT on an Investors page (" + shops.name() + ")", !String.join("\n", fx(played::signature)).contains("\"ITS EQUIPMENT\""));
    }

    /**
     * Section 97: a NEW build card, kept. The category page holding the fixture's new template (the Electric Arc Furnace, found here, Iron & Steel's)
     * shows NEW on its card - the chip in the head, the violet edge - and where it came from, the strip under the page's head says what is new and the
     * category's chip carries a dot, on its own page and on every other; and the page kept over last month's nodes is the page built new. The seen
     * rule from both sides: NEW the month the card is first drawn (a redraw in that month too), and not the month after while the model alone still says
     * NEW - the find put back to month 9, so that the fixture's furnace is five months old when first drawn and six the month after, and the model's
     * rule (a year) has nothing to do with it - and the dot follows the one helper. The lab's card says what a lab is for, a played city's pages have
     * no NEW at all.
     */
    static void researchBuild(Game g, KeptBench played) throws Exception {
        System.out.println("\n--- 97: a NEW build card, kept ---");
        Game town = ResearchViewCheck.researchTown();
        java.util.Map<String, Integer> finds = new java.util.HashMap<>(town.getResearch().foundHere());
        finds.put("PE1", 9);
        ResearchViewCheck.setFinds(town, finds);
        KeptBench bench = fx(() -> new KeptBench(town));
        BuildingsTemplate furnace = ResearchViewCheck.template(town, "Electric Arc Furnace");
        ResearchView.Origin origin = ResearchView.origin(town, furnace);
        List<BuildingsTemplate> fresh = ResearchView.newTemplates(town);
        check("the town's furnace is NEW, found here in month 9, five months old (the page's premise)", fresh.contains(furnace) && origin != null
                && origin.source() == ResearchView.Source.HERE && origin.month() == 9 && town.getMonth() - origin.month() == 5);
        if (!fresh.contains(furnace) || origin == null) return;
        String cat = BuildAdvice.categoryOf(furnace).name();
        java.util.Set<String> holding = new java.util.HashSet<>();
        for (BuildingsTemplate t : fresh) holding.add(BuildAdvice.categoryOf(t).name());
        String other = null;
        for (BuildAdvice.Category c : BuildAdvice.categories()) {
            if (!c.cityBuilds() && !holding.contains(c.name())) { other = c.name(); break; }
        }
        final String away = other;
        check("the town has a market category with nothing new on it (" + away + "), to look at the dot from", away != null);
        if (away == null) return;
        KeptBench.Page page = fx(() -> bench.build("Build, a category of the town", away, java.util.Map.of()));
        int month = town.getMonth();

        // The month the page is first drawn, from a page with nothing new: the furnace's category carries the dot, and nobody has seen its card.
        check("no card has been drawn: the furnace shows NEW and was seen in no month", bench.buildShowsNew(furnace) && bench.buildNewSeen(furnace) == null);
        arrive(bench, page);
        check("on " + away + "'s page (nothing new on it) " + cat + "'s chip carries the dot, and no strip says what is new",
                fx(bench::buildNewDots).contains(cat) && fx(bench::buildNewStrip) == null);
        check("...and the furnace is still unseen (its card has not been drawn)", bench.buildNewSeen(furnace) == null && bench.buildShowsNew(furnace));

        // The category's page, the month the card is first drawn.
        fx(() -> {
            bench.buildOpen(cat);
            bench.drawKept(page);
            bench.passes();
            return null;
        });
        check("the card is drawn in month " + month + ": the screen notes that month for it", Integer.valueOf(month).equals(bench.buildNewSeen(furnace)));
        check("...it shows NEW: the chip in its head and the violet edge", bench.buildShowsNew(furnace) && fx(bench::buildNewChips) >= 1
                && fx(bench::buildNewEdges) >= 1);
        String strip = fx(bench::buildNewStrip);
        String place = "the " + furnace.getName() + " (" + (origin.source() == ResearchView.Source.HERE ? "found here, " : "from abroad, ")
                + CityCalendar.format(origin.month()) + ")";
        check("...the strip under the head says what is new (" + place + ")", strip != null && strip.startsWith("New: ") && strip.contains(place));
        check("...the chip of " + cat + " carries the dot", fx(bench::buildNewDots).contains(cat));
        String shown = String.join("\n", fx(bench::signature));
        String line = String.format(java.util.Locale.ROOT, "\"Found here, %s · %s found it, %.1f years before the world\"", CityCalendar.format(origin.month()),
                origin.entry().field().school(), origin.yearsAhead());
        check("...and the card says where it came from (" + line + ")", shown.contains(line));
        String d1 = fx(() -> againstNew(bench, page));
        if (d1 != null) System.out.println("  first difference: " + d1);
        check("the kept page of " + cat + " in month " + month + " equals a page built new", d1 == null);
        // KP2b's rule (section 92's nothingSet()) on the page with a NEW card, its strip and the dot, which no default city has.
        nothingSet(bench, page, "Build, " + cat + " with the NEW furnace in the town", b -> { b.buildOpen(cat); return null; });

        // A redraw in the same month still shows NEW: seen this month is not seen in an earlier one.
        fx(() -> {
            bench.drawKept(page);
            bench.passes();
            return null;
        });
        check("drawn again in the same month the card still shows NEW (a month's own redraws are the same month)", bench.buildShowsNew(furnace)
                && fx(bench::buildNewChips) >= 1);

        // The month after: the model alone still says NEW (a year is the bound), and the card was seen in an earlier month.
        Object[] next = fx(() -> {
            month(town);
            bench.drawKept(page);
            bench.passes();
            return new Object[]{ResearchView.isNew(town, furnace), againstNew(bench, page)};
        });
        check("the month after, the model alone still says NEW (the premise of the seen rule)", (boolean) next[0]);
        check("...and the screen does not: the card was first drawn in month " + month + ", an earlier month", !bench.buildShowsNew(furnace));
        check("...no NEW chip, no violet edge and no strip on " + cat + "'s page", fx(bench::buildNewChips) == 0 && fx(bench::buildNewEdges) == 0
                && fx(bench::buildNewStrip) == null);
        check("...and no dot on its chip, which follows the same rule", !fx(bench::buildNewDots).contains(cat));
        if (next[1] != null) System.out.println("  first difference: " + next[1]);
        check("the kept page the month after equals a page built new", next[1] == null);
        check("...the same on the page of " + away + ", whose chip for " + cat + " has lost its dot too", fx(() -> {
            bench.buildOpen(away);
            bench.drawKept(page);
            bench.passes();
            return !bench.buildNewDots().contains(cat) && againstNew(bench, page) == null;
        }));

        // A reload is a new city: nothing is remembered, and what is within the year shows NEW again.
        check("a reload (the screen forgotten) shows NEW again on what became known within the year", fx(() -> {
            bench.home(page);
            return bench.buildShowsNew(furnace);
        }));

        // The rule on its own, with a seen map and two months.
        check("the rule alone: NEW for a card never drawn, and for one drawn this month", KeptBench.buildShowsNew(true, null, 14)
                && KeptBench.buildShowsNew(true, 14, 14));
        check("...and not for one drawn in an earlier month, nor for what the model says is not new", !KeptBench.buildShowsNew(true, 13, 14)
                && !KeptBench.buildShowsNew(false, null, 14) && !KeptBench.buildShowsNew(false, 14, 14));

        // A lab's card says what a lab is for.
        BuildingsTemplate lab = ResearchViewCheck.template(town, "Engineering Research Lab");
        String labCat = BuildAdvice.categoryOf(lab).name();
        fx(() -> {
            bench.buildOpen(labCat);
            bench.drawKept(page);
            bench.passes();
            return null;
        });
        String labs = String.join("\n", fx(bench::signature));
        String about = "A lab is the investors' building for a prize: " + Research.LAB_POSTS + " researchers of one field";
        check("a lab's card (" + labCat + ") says what a lab is for, its numbers from the model's constants (" + Research.LAB_POSTS + " posts, "
                + Research.LAB_IDLE_MONTHS + " months idle)", labs.contains(about) && labs.contains(Research.LAB_IDLE_MONTHS + " months idle."));
        check("...and in a city founded the 1900 way says no more than that", !labs.contains("founded the default way"));
        String d2 = fx(() -> againstNew(bench, page));
        if (d2 != null) System.out.println("  first difference: " + d2);
        check("the lab's page kept equals a page built new", d2 == null);

        // The played city: no NEW anywhere, and a lab there says it only idles.
        check("a city founded the default way has no NEW in the model", ResearchView.newTemplates(g).isEmpty());
        KeptBench.Page labPage = fx(() -> played.build("Build, " + labCat + " in the played city", labCat, java.util.Map.of()));
        arrive(played, labPage);
        String playedLabs = String.join("\n", fx(played::signature));
        check("...no NEW chip, edge, strip or dot on " + labCat + "'s page", fx(played::buildNewChips) == 0 && fx(played::buildNewEdges) == 0
                && fx(played::buildNewStrip) == null && fx(played::buildNewDots).isEmpty());
        check("...and a lab's card there says that nothing is ever on the chase in such a city", playedLabs.contains(about)
                && playedLabs.contains("founded the default way"));
    }

    /** Sections 47 to 54: the Sectors tab kept. */
    static void sectors(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== Sectors (KP4) ===");
        System.out.println("  " + fx(() -> ensureWorks(g)));
        SectorKinds kinds = fx(() -> sectorKinds(g));
        String plain = kinds.plain(), works = kinds.materials();
        System.out.println("  a plain business with a plant standing: " + plain + "; a materials industry with works to draw: " + works);
        check("the city has a plain business with a plant standing (" + plain + ")", plain != null);
        check("...and a materials industry with works standing (" + works + ")", works != null);
        if (plain == null || works == null) return;
        // The works are held while the pages are watched: a sawmill with no logs idles, and the city sheds idle works (O5's rule), which would
        // put the page through a shape of works that come and go; held, what stands is the fixture's (as RefineryViewCheck's refiners are).
        g.getBusinessInvestment().holdSector(works);
        final String plainName = called(g, plain), worksName = called(g, works), shopsName = called(g, Sectors.RETAIL);
        final String ops = KeptBench.sectorPages().get(0);
        // The clock paused: a month stepped by hand counts up at any speed, so the pages are drawn with their animations running
        // (and finished where the signature is read), the harder case for a page kept.
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });

        KeptBench.Page list = fx(bench::sectors);
        KeptBench.Page plainOps = fx(() -> bench.sectors(possessive(plainName) + " Operations page", plain, ops, false));
        pageKept(g, bench, list, 47);
        pageKept(g, bench, plainOps, 47);

        List<KeptBench.Page> shown = new ArrayList<>(List.of(list, plainOps));
        shown.add(fx(() -> bench.sectors(possessive(plainName) + " Income page", plain, "Income", false)));
        shown.add(fx(() -> bench.sectors(possessive(plainName) + " Income page as a statement", plain, "Income", true)));
        shown.add(fx(() -> bench.sectors(possessive(plainName) + " Balance sheet as a statement", plain, "Balance sheet", true)));
        for (KeptBench.Page p : shown) {
            arrive(bench, p);
            pageShows(g, bench, p, 48);
        }

        // 49: what reshapes the Sectors tab.
        KeptBench.Page tab = fx(() -> bench.sectors("the Sectors tab", plain, ops, false));
        pageReshaped(g, bench, list, 49, List.of(
                new Choice("Show more (every card open to its second row)", b -> { b.sectorsOpen(null, ops, false); b.sectorsMore(true); return null; }, true),
                choice("Show less", b -> { b.sectorsMore(false); return null; })));
        List<Choice> every = new ArrayList<>();
        for (String key : Sectors.KEYS) every.add(choice(called(g, key) + " on its Operations page", on(key, ops, false)));
        pageReshaped(g, bench, tab, 49, every);
        // One business of each kind on all five pages, in both views: Operations has no statement.
        List<Choice> pages = new ArrayList<>();
        for (String key : new String[]{plain, Sectors.RETAIL, Sectors.REAL_ESTATE, works, Sectors.FREIGHT}) {
            for (String page : KeptBench.sectorPages()) {
                pages.add(choice(called(g, key) + ", " + page, on(key, page, false)));
                if (!page.equals(ops)) pages.add(choice(called(g, key) + ", " + page + " as a statement", on(key, page, true)));
            }
        }
        pageReshaped(g, bench, tab, 49, pages);
        // Freight's four divisions, each a page of its own.
        List<Choice> divisions = new ArrayList<>();
        for (String division : KeptBench.freightDivisions()) {
            divisions.add(choice("Freight on its " + division + " division", b -> { b.sectorsOpen(Sectors.FREIGHT, ops, false); b.sectorsDivision(division); return null; }));
        }
        divisions.add(choice("Freight on All again", b -> { b.sectorsOpen(Sectors.FREIGHT, ops, false); b.sectorsDivision("All"); return null; }));
        pageReshaped(g, bench, tab, 49, divisions);
        // A fold opened has to add to the page, and closed take it away; a note, every note, the toolbar's two columns.
        // The premise first: the note chosen is one the statement opens on lines this month (on 0.9.19 the builders' revenue,
        // note 1, came down to one named part, which draws no caret, and the fold chosen by "the first note" caused nothing).
        int note = fx(() -> bench.sectorsNoteOf(plain));
        System.out.println("  the first note on " + possessive(plainName) + " income statement that opens on lines: " + note);
        check(possessive(plainName) + " income statement has a note that opens on lines this month (the fold's premise)", note > 0);
        List<Choice> folds = new ArrayList<>();
        folds.add(choice(possessive(plainName) + " Operations page", on(plain, ops, false)));
        folds.add(new Choice("the fold of every line opened on it", b -> { b.sectorsOpen(plain, ops, false); b.sectorsFold("every", plain, true); return null; }, true));
        folds.add(choice("...and closed", b -> { b.sectorsFold("every", plain, false); return null; }));
        folds.add(choice(possessive(shopsName) + " Operations page", on(Sectors.RETAIL, ops, false)));
        folds.add(new Choice("the foods fold opened on it (the goods one row folds)", b -> { b.sectorsOpen(Sectors.RETAIL, ops, false); b.sectorsFold("foods", Sectors.RETAIL, true); return null; }, true));
        folds.add(choice("...and closed", b -> { b.sectorsFold("foods", Sectors.RETAIL, false); return null; }));
        folds.add(choice(possessive(shopsName) + " Investors page", on(Sectors.RETAIL, "Investors", false)));
        folds.add(new Choice("the order book opened on its owners' card (the Investors page)", b -> { b.sectorsOpen(Sectors.RETAIL, "Investors", false); b.sectorsFold("book", Sectors.RETAIL, true); return null; }, true));
        folds.add(choice("...and closed", b -> { b.sectorsFold("book", Sectors.RETAIL, false); return null; }));
        folds.add(choice(possessive(plainName) + " Income page as a statement", on(plain, "Income", true)));
        folds.add(new Choice("note " + note + " opened on it", b -> { b.sectorsOpen(plain, "Income", true); b.sectorsNote(plain, "income", note, true); return null; }, true));
        folds.add(choice("...and closed", b -> { b.sectorsNote(plain, "income", note, false); return null; }));
        folds.add(new Choice("every note opened", b -> { b.sectorsOpen(plain, "Income", true); b.sectorsNotes(plain, true); return null; }, true));
        folds.add(choice("...and every note closed", b -> { b.sectorsNotes(plain, false); return null; }));
        folds.add(choice("the change column off", b -> { b.sectorsOpen(plain, "Income", true); b.sectorsColumns(false, true); return null; }));
        folds.add(choice("...the common-size column off too", b -> { b.sectorsColumns(false, false); return null; }));
        folds.add(choice("...the change column on again", b -> { b.sectorsColumns(true, false); return null; }));
        folds.add(choice("...and both on", b -> { b.sectorsColumns(true, true); return null; }));
        pageReshaped(g, bench, tab, 49, folds);

        sectorsPictures(g, bench, works, worksName);
        sectorsCountUp(g, bench, plain);

        // 52: what the kept month costs: the list, and the three business pages that weigh most.
        arrive(bench, list);
        pageCost(g, bench, list, 52, KEPT_SECTORS_MS, "KEPT_SECTORS_MS");
        for (KeptBench.Page p : List.of(
                fx(() -> bench.sectors(possessive(shopsName) + " Operations page", Sectors.RETAIL, ops, false)),
                fx(() -> bench.sectors(possessive(plainName) + " Income page as a statement", plain, "Income", true)),
                fx(() -> bench.sectors(possessive(worksName) + " Operations page", works, ops, false)))) {
            pageCost(g, bench, p, 52, KEPT_SECTOR_MS, "KEPT_SECTOR_MS");
        }

        g.getBusinessInvestment().releaseSector(works);
        sectorsControls();
        sectorsHover(g, bench, plain);
    }

    /**
     * Section 50: the pictures a business's page holds are kept. A materials industry's picture (IndustryPane) and the leg bars
     * (LegBar) of the city's own, and the refinery's (RefineryPicture) in a town that has one: each stays the node it was from
     * month to month, repaints only where the month's view moved, keeps the shapes it paints (nine in ten), is not painted again by
     * a redraw in the same month, and equals the picture a page built new paints.
     */
    static void sectorsPictures(Game g, KeptBench bench, String works, String worksName) throws Exception {
        System.out.println("\n--- 50: the pictures on a business's page are kept ---");
        final String ops = KeptBench.sectorPages().get(0);
        KeptBench.Page industry = fx(() -> bench.sectors(possessive(worksName) + " Operations page", works, ops, false));
        picturesKept(g, bench, industry, "a materials industry's picture (" + worksName + ")",
                b -> b.industryPane() == null ? List.of() : List.of(b.industryPane()), true);
        KeptBench.Page legs = fx(() -> bench.sectors(possessive(called(g, Sectors.RETAIL)) + " Operations page", Sectors.RETAIL, ops, false));
        picturesKept(g, bench, legs, "the legs' bars (" + called(g, Sectors.RETAIL) + ")", KeptBench::legBars, false);

        // The refinery: a town of its own (RefineryViewCheck's: a refinery with a campus of units, two land wells on fiat sites, played two years).
        Game town = fx(() -> RefineryViewCheck.town("keptcheck-refinery", RefineryViewCheck.WELLS, RefineryViewCheck.CAMPUS));
        fx(() -> quietly(() -> {
            town.simulateMonths(RefineryViewCheck.MONTHS);
            return null;
        }));
        KeptBench refineryBench = fx(() -> new KeptBench(town));
        boolean drawn = fx(() -> RefineryView.of(town).drawn());
        check("the refinery's town has a refinery that runs, so that its picture is drawn (" + drawn + ")", drawn);
        if (!drawn) return;
        fx(() -> {
            refineryBench.clock(false, null);
            return null;
        });
        KeptBench.Page refinery = fx(() -> refineryBench.sectors("the refinery's Operations page", Sectors.REFINING, ops, false));
        picturesKept(town, refineryBench, refinery, "the refinery's picture",
                b -> b.refineryPicture() == null ? List.of() : List.of(b.refineryPicture()), true);
    }

    /**
     * What a month does to the pictures a page holds (`find` takes them off the page; `paints` for a picture that is told the
     * month's view and paints it, as the refinery's and a materials industry's are, and not for a bar that keeps its parts). The
     * pictures are paired by identity: a page can gain a picture between two months (a leg's bar for a leg that begins), and the
     * ones after it move up the list.
     */
    static void picturesKept(Game g, KeptBench bench, KeptBench.Page page, String what,
                             java.util.function.Function<KeptBench, List<javafx.scene.layout.Pane>> find, boolean paints) throws Exception {
        arrive(bench, page);
        List<javafx.scene.layout.Pane> first = fx(() -> {
            bench.settle();
            return find.apply(bench);
        });
        int parts = fx(() -> {
            int n = 0;
            for (javafx.scene.layout.Pane p : find.apply(bench)) n += KeptBench.partsOf(p).size();
            return n;
        });
        System.out.println("  " + what + ": " + first.size() + " on the page, with " + parts + " parts");
        check(what + " is on the page of this city (" + first.size() + ")", !first.isEmpty());
        if (first.isEmpty()) return;
        int repainted = 0, quiet = 0, differ = 0;
        long nodesKept = 0, nodesHad = 0, kept = 0, had = 0;
        String firstDiffer = null;
        for (int m = 1; m <= PICTURE_MONTHS; m++) {
            Object[] r = fx(() -> {
                java.util.Map<javafx.scene.layout.Pane, Object[]> before = new java.util.IdentityHashMap<>();
                for (javafx.scene.layout.Pane p : find.apply(bench)) before.put(p, new Object[]{KeptBench.painted(p), KeptBench.partsOf(p)});
                month(g);
                bench.drawKept(page);
                bench.passes();
                List<javafx.scene.layout.Pane> now = find.apply(bench);
                int repaints = 0, held = 0;
                long keptShapes = 0, hadShapes = 0;
                for (javafx.scene.layout.Pane p : now) {
                    Object[] was = before.get(p);
                    if (was == null) continue;
                    held++;
                    if (paints && KeptBench.painted(p) != was[0]) repaints++;
                    @SuppressWarnings("unchecked") List<javafx.scene.Node> had2 = (List<javafx.scene.Node>) was[1];
                    java.util.Set<javafx.scene.Node> now2 = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
                    now2.addAll(KeptBench.partsOf(p));
                    for (javafx.scene.Node n : had2) if (now2.contains(n)) keptShapes++;
                    hadShapes += had2.size();
                }
                // The same month drawn again: the view is the same, so nothing is painted and no part is made or lost.
                java.util.Map<javafx.scene.layout.Pane, Object[]> landed = new java.util.IdentityHashMap<>();
                for (javafx.scene.layout.Pane p : now) landed.put(p, new Object[]{KeptBench.painted(p), KeptBench.partsOf(p)});
                bench.drawKept(page);
                bench.passes();
                List<javafx.scene.layout.Pane> again = find.apply(bench);
                boolean still = again.equals(now);
                for (javafx.scene.layout.Pane p : again) {
                    Object[] was = landed.get(p);
                    still &= was != null && KeptBench.painted(p) == was[0] && KeptBench.partsOf(p).equals(was[1]);
                }
                String d = againstNew(bench, page);
                return new Object[]{held, before.size(), repaints, keptShapes, hadShapes, still, d};
            });
            nodesKept += (int) r[0];
            nodesHad += (int) r[1];
            if ((int) r[2] > 0) repainted++;
            kept += (long) r[3];
            had += (long) r[4];
            if ((boolean) r[5]) quiet++;
            if (r[6] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[6];
            }
        }
        System.out.printf("  %d months: %d of %d pictures handed back, %d repainted; %d of %d shapes kept across them%n",
                PICTURE_MONTHS, nodesKept, nodesHad, repainted, kept, had);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check(what + ": each month's redraw hands back nine in ten of the pictures it had, or more (" + nodesKept + " of " + nodesHad + ")",
                nodesKept * 10 >= nodesHad * 9);
        if (paints) check(what + ": a month whose view moved repaints it (" + repainted + " of " + PICTURE_MONTHS + " months did)", repainted > 0);
        check(what + ": each month keeps nine in ten of the parts it had, or more (" + kept + " of " + had + ")", kept * 10 >= had * 9);
        check(what + ": a redraw in the same month paints nothing and makes no part (" + quiet + " of " + PICTURE_MONTHS + ")", quiet == PICTURE_MONTHS);
        check(what + ": the page equals a page built new at every month (" + differ + " differ)", differ == 0);
    }

    /** The page as it stands with no animation finished first, against the same page built new: the first difference, or null. */
    static String againstNewAsIs(KeptBench bench, KeptBench.Page page) throws Exception {
        bench.passes();
        bench.settle();
        List<String> kept = bench.signature();
        bench.showFresh(page);
        bench.settle();
        List<String> fresh = bench.signature();
        bench.restoreKeptPage();
        return firstDifference(kept, fresh);
    }

    /** Whether the figures the Sectors pages count have moved between last month and this: the list's KEPT, a business's, and some card's. */
    private static boolean figuresMoved(String[][] words, boolean business) {
        boolean cards = false;
        for (int i = 2; i < words.length; i++) cards |= !words[i][0].equals(words[i][1]);
        return !words[0][0].equals(words[0][1]) && cards && (!business || !words[1][0].equals(words[1][1]));
    }

    /** The figures on a Sectors page that count up: the KEPT, and on the list every card's net income. */
    private static List<javafx.scene.control.Label> countingFigures(KeptBench bench, String key) {
        List<javafx.scene.control.Label> out = new ArrayList<>();
        out.add(bench.sectorsKept());
        if (key == null) out.addAll(bench.sectorsCardFigures());
        return out;
    }

    /**
     * One clock speed of section 51 on one page (`key` null for the list): the page is shown, a month lands and it is shown again; a count
     * starts on each figure that moved exactly where `expect` says, the waterfall grows and the ring sweeps then and not otherwise, and
     * every figure reads this month's words (at once where nothing counts, after the counts where they do). Null, or what was wrong.
     */
    static String countRung(Game g, KeptBench bench, String key, String page, int at, boolean paused, boolean expect) throws Exception {
        bench.clock(!paused, null);
        bench.speed(at);
        bench.sectorsOpen(key, page, false);
        bench.showSectors();
        bench.finishPageAnimations();
        bench.passes();
        List<Animation> before = new ArrayList<>();
        for (javafx.scene.control.Label l : countingFigures(bench, key)) before.add(KeptBench.countOf(l));
        Animation fallBefore = bench.fallGrowth(), sweepBefore = bench.ringSweep();
        if (!landUntil(g, () -> figuresMoved(bench.sectorsWords(key), key != null))) return "no month in twelve moved the figures";
        bench.showSectors();
        bench.passes();
        String[][] words = bench.sectorsWords(key);
        List<javafx.scene.control.Label> figures = countingFigures(bench, key);
        if (figures.size() != before.size() || figures.contains(null)) return "the page has no figure to count";
        StringBuilder bad = new StringBuilder();
        int cardsMoved = 0, cardsStarted = 0, cardsWrong = 0;
        for (int i = 0; i < figures.size(); i++) {
            javafx.scene.control.Label figure = figures.get(i);
            boolean started = KeptBench.countOf(figure) != before.get(i) && KeptBench.counting(figure);
            if (i == 0) {
                if (started != expect) bad.append(expect ? "KEPT did not count" : "KEPT counted").append("; ");
            } else {
                String[] w = words[1 + i];
                if (!w[0].equals(w[1])) cardsMoved++;
                if (started) {
                    cardsStarted++;
                    if (w[0].equals(w[1])) cardsWrong++;
                }
            }
            if (!expect && !figure.getText().equals(i == 0 ? words[key == null ? 0 : 1][1] : words[1 + i][1])) {
                bad.append("a figure reads \"").append(figure.getText()).append("\" at once, not this month's; ");
            }
        }
        if (key == null) {
            if (expect && cardsStarted == 0) bad.append("no card counted; ");
            if (!expect && cardsStarted > 0) bad.append(cardsStarted).append(" cards counted; ");
            if (cardsWrong > 0) bad.append(cardsWrong).append(" cards counted a figure that did not move; ");
        }
        if (bench.hasFall()) {
            Animation fall = bench.fallGrowth();
            boolean grows = fall != fallBefore && fall != null && fall.getStatus() != Animation.Status.STOPPED;
            if (grows != expect) bad.append(expect ? "the waterfall did not grow" : "the waterfall grew").append("; ");
        }
        if (key != null && page.equals(KeptBench.sectorPages().get(0)) && bench.hasRing()) {
            Animation sweep = bench.ringSweep();
            boolean sweeps = sweep != sweepBefore && sweep != null && sweep.getStatus() != Animation.Status.STOPPED;
            if (sweeps != expect) bad.append(expect ? "the ring did not sweep" : "the ring swept").append("; ");
        }
        if (!expect) {
            // Nothing counts, so the page is already the page a month's figures draw: equal to a page built new, with no animation finished first.
            KeptBench.Page p = bench.sectors("the page", key, page, false);
            String d = againstNewAsIs(bench, p);
            if (d != null) bad.append("not the page built new: ").append(d.replace('\n', ' ')).append("; ");
        } else {
            bench.finishPageAnimations();
            bench.passes();
            for (int i = 0; i < figures.size(); i++) {
                String reads = figures.get(i).getText(), want = i == 0 ? words[key == null ? 0 : 1][1] : words[1 + i][1];
                if (!reads.equals(want)) bad.append("a figure reads \"").append(reads).append("\" after its count, not \"").append(want).append("\"; ");
            }
        }
        return bad.length() == 0 ? null : "at " + (paused ? "a paused clock" : KeptBench.speedLadder()[at] + "x") + " on " + (key == null ? "the list" : page) + ": " + bad;
    }

    /**
     * Section 51: the count-up only at 1x-10x, on the list's KEPT figure and its cards', a business's KEPT, the Income page's waterfall
     * and the Operations page's ring. With the clock at each rung of the speed ladder a month that lands starts a count on every figure
     * that moved exactly at the rungs UserInterface.countsUp() allows (COUNT_UP_TOP) and when the clock is paused; past it nothing counts
     * and the page is the page a month's figures draw; a count still running when a month lands past it is stopped.
     */
    static void sectorsCountUp(Game g, KeptBench bench, String plain) throws Exception {
        System.out.println("\n--- 51: the count-up only at 1x-10x (Sectors) ---");
        double[] ladder = KeptBench.speedLadder();
        double top = KeptBench.countUpTop();
        int countingRungs = 0;
        for (double speed : ladder) if (speed <= top) countingRungs++;
        System.out.printf("  the ladder has %d rungs, %d of them at or under COUNT_UP_TOP (%.0fx)%n", ladder.length, countingRungs, top);
        String[][] views = {{null, "Operations", "the list's KEPT and its cards'"}, {plain, "Income", "a business's KEPT and the Income page's waterfall"},
                {plain, "Operations", "a business's KEPT and the Operations page's ring"}};
        for (String[] view : views) {
            StringBuilder wrong = new StringBuilder();
            fx(() -> {
                bench.forgetSectors();
                return null;
            });
            for (int rung = 0; rung <= ladder.length; rung++) {
                // The last pass is the clock stopped, at the top rung: a month stepped by hand counts at any speed.
                boolean paused = rung == ladder.length;
                int at = paused ? ladder.length - 1 : rung;
                boolean expect = paused || ladder[at] <= top;
                String result = fx(() -> countRung(g, bench, view[0], view[1], at, paused, expect));
                if (result != null) wrong.append(wrong.length() == 0 ? "" : "; ").append(result);
            }
            if (wrong.length() > 0) System.out.println("  " + view[2] + ": " + wrong);
            check("a month landing starts " + view[2] + " counting exactly at the rungs countsUp() allows, and when paused (" + (ladder.length + 1)
                    + " clocks)", wrong.length() == 0);

            // A count running when the clock has been sped past the top: the next month sets the figures at once and stops it.
            String stopped = fx(() -> {
                int slow = 0;
                while (ladder[slow] > top) slow++;
                bench.clock(true, null);
                bench.speed(slow);
                bench.sectorsOpen(view[0], view[1], false);
                bench.showSectors();
                bench.finishPageAnimations();
                bench.passes();
                if (!landUntil(g, () -> figuresMoved(bench.sectorsWords(view[0]), view[0] != null))) return "no month in twelve moved the figures";
                bench.showSectors();
                bench.passes();
                List<javafx.scene.control.Label> figures = countingFigures(bench, view[0]);
                if (figures.contains(null) || !KeptBench.counting(figures.get(0))) return "no count-up was running to stop";
                List<Animation> running = new ArrayList<>();
                for (javafx.scene.control.Label l : figures) running.add(KeptBench.countOf(l));
                Animation fall = bench.fallGrowth(), sweep = bench.ringSweep();
                bench.speed(ladder.length - 1);
                if (!landUntil(g, () -> figuresMoved(bench.sectorsWords(view[0]), view[0] != null))) return "no month in twelve moved the figures";
                bench.showSectors();
                bench.passes();
                String[][] words = bench.sectorsWords(view[0]);
                for (int i = 0; i < figures.size(); i++) {
                    Animation a = running.get(i);
                    if (a != null && a.getStatus() != Animation.Status.STOPPED) return "a count-up is still " + a.getStatus() + " on a figure";
                    String want = i == 0 ? words[view[0] == null ? 0 : 1][1] : words[1 + i][1];
                    if (!figures.get(i).getText().equals(want)) return "a figure reads \"" + figures.get(i).getText() + "\", not \"" + want + "\"";
                }
                if (fall != null && fall.getStatus() != Animation.Status.STOPPED) return "the waterfall's growth is " + fall.getStatus();
                if (sweep != null && sweep.getStatus() != Animation.Status.STOPPED) return "the ring's sweep is " + sweep.getStatus();
                String d = againstNewAsIs(bench, bench.sectors("the page", view[0], view[1], false));
                return d == null ? null : "the page is not the page built new: " + d.replace('\n', ' ');
            });
            if (stopped != null) System.out.println("  " + view[2] + ": " + stopped);
            check("a count running when a month lands past COUNT_UP_TOP is stopped on " + view[2] + ", and each figure is the month's", stopped == null);
        }
    }

    /** The oil reserve's city: OilViewCheck's sea town (a platform with its wells, two land wells, a Strategic Reserve with FILL ordered), its fixture's report lines kept off this output. */
    static Game seaTown() throws Exception {
        PrintStream[] was = {OilViewCheck.out, WellCheck.out, WellCheck.quiet, OilCheck.out, OilCheck.quiet, MiningCheck.out};
        OilViewCheck.out = QUIET;
        WellCheck.out = QUIET;
        WellCheck.quiet = QUIET;
        OilCheck.out = QUIET;
        OilCheck.quiet = QUIET;
        MiningCheck.out = QUIET;
        try {
            return fx(OilViewCheck::seaTown);
        } finally {
            OilViewCheck.out = was[0];
            WellCheck.out = was[1];
            WellCheck.quiet = was[2];
            OilCheck.out = was[3];
            OilCheck.quiet = was[4];
            MiningCheck.out = was[5];
        }
    }

    /**
     * Section 53: a control keeps the player's hand. The oil reserve's two dials (FILL, then RELEASE: sliders on Levers.dialCard's
     * ladders), through the screen's real show method, in the sea town: pressed, a slider is held and a month landing under it leaves
     * the same node, its thumb, its reading and its ladder's shown value where the player put them; released, it stages the thumb and
     * the page equals a page built new; a value set outside any draw reaches the reading of the latest draw.
     */
    static void sectorsControls() throws Exception {
        System.out.println("\n--- 53: a control keeps the player's hand (the oil reserve's dials) ---");
        Game sea = seaTown();
        check("the sea town has a Strategic Reserve with oil in it, to draw its two dials (" + (sea == null ? "no town" : sea.getReserve().getTonnes() + " t") + ")",
                sea != null && sea.getReserve().getTonnes() > 0);
        if (sea == null) return;
        KeptBench seaBench = fx(() -> new KeptBench(sea));
        fx(() -> {
            seaBench.clock(false, null);
            return null;
        });
        final String ops = KeptBench.sectorPages().get(0);
        KeptBench.Page oil = fx(() -> seaBench.sectors("the oil industry's Operations page", Sectors.OIL, ops, false));
        int dials = fx(() -> {
            seaBench.home(oil);
            seaBench.showSectors();
            seaBench.passes();
            return seaBench.sliders().size();
        });
        check("the oil industry's page has the reserve's two dials to hold (" + dials + " sliders)", dials >= 2);
        if (dials < 2) return;
        dial(sea, seaBench, oil, 0, "FILL", "reserveFill", RefineryView::tonnes);
        dial(sea, seaBench, oil, 1, "RELEASE", "reserveRelease", t -> RefineryView.tonnes(t) + " a month");
    }

    /** One of the reserve's dials (the `which`th slider on the page), pressed, held across a month, released, and set outside a draw. */
    static void dial(Game sea, KeptBench bench, KeptBench.Page page, int which, String name, String key,
                     java.util.function.DoubleFunction<String> words) throws Exception {
        Object[] r = fx(() -> {
            bench.showSectors();
            bench.passes();
            javafx.scene.control.Slider bar = bench.sliders().get(which);
            double step = bar.getBlockIncrement();
            double snapped = bar.getMin() + Math.round((bar.getValue() - bar.getMin()) / step) * step;
            double thumb = snapped + 2 * step <= bar.getMax() + 1e-9 ? snapped + 2 * step : snapped - 2 * step;
            String before = KeptBench.readingOf(bar).getText();
            // (a) pressed, dragged two steps, a month lands.
            KeptBench.press(bar);
            boolean heldPressed = KeptBench.held(bar);
            bar.setValueChanging(true);
            bar.setValue(thumb);
            String dragged = KeptBench.readingOf(bar).getText();
            month(sea);
            bench.showSectors();
            bench.passes();
            boolean sameBar = bench.sliders().size() > which && bench.sliders().get(which) == bar;
            String kept = KeptBench.readingOf(bar).getText();
            double shown = KeptBench.shownBy(bar);
            boolean heldStill = KeptBench.held(bar);
            double valueNow = bar.getValue();
            double stagedMid = bench.staged(key);
            Object[] a = {heldPressed, sameBar, valueNow, thumb, kept, dragged, before, shown, heldStill, stagedMid, words.apply(thumb)};
            // (b) released: the thumb is staged, the screen redrawn, the page the page built new.
            bar.setValueChanging(false);
            KeptBench.release(bar);
            bench.passes();
            double staged = bench.staged(key);
            boolean heldAfter = KeptBench.held(bar);
            boolean same = bench.sliders().size() > which && bench.sliders().get(which) == bar;
            String differs = againstNew(bench, page);
            Object[] b = {heldAfter, staged, same, differs};
            // (c) several kept months, then a value set outside any draw: the reading of the latest draw follows.
            for (int i = 0; i < 4; i++) {
                month(sea);
                bench.showSectors();
                bench.passes();
            }
            javafx.scene.control.Slider later = bench.sliders().get(which);
            double other = thumb - 2 * step >= bar.getMin() - 1e-9 ? thumb - 2 * step : thumb + 2 * step;
            later.setValue(other);
            Object[] c = {later == bar, KeptBench.readingOf(later).getText(), words.apply(other)};
            bench.unstage(key);
            bench.showSectors();
            bench.passes();
            return new Object[]{a, b, c, bench.staged(key)};
        });
        Object[] a = (Object[]) r[0], b = (Object[]) r[1], c = (Object[]) r[2];
        System.out.printf("  %s dragged two steps: reading \"%s\" -> \"%s\"; a month later \"%s\", the ladder shows %.4f for a thumb at %.4f%n",
                name, a[6], a[5], a[4], (double) a[7], (double) a[3]);
        check(name + ": a slider pressed is held", (boolean) a[0]);
        check(name + ": a month landing on a held slider leaves the same slider node, still held", (boolean) a[1] && (boolean) a[8]);
        check(name + ": ...its value still the thumb's (" + a[2] + ")", Math.abs((double) a[2] - (double) a[3]) < 1e-9);
        check(name + ": ...the reading the thumb's words (\"" + a[4] + "\", not the staged \"" + a[6] + "\")", a[4].equals(a[10]) && !a[4].equals(a[6]));
        check(name + ": ...Ladder.shown() the thumb's value, not the staged one", Math.abs((double) a[7] - (double) a[3]) < 1e-9);
        check(name + ": ...and nothing is staged before the release", Double.isNaN((double) a[9]));
        check(name + ": a slider released is not held", !(boolean) b[0]);
        check(name + ": ...the dial is staged at the thumb (" + b[1] + ")", Math.abs((double) b[1] - (double) a[3]) < 1e-9);
        check(name + ": ...and the page is the same slider, kept, equal to a page built new", (boolean) b[2] && b[3] == null);
        if (b[3] != null) System.out.println("  " + b[3]);
        check(name + ": after several kept months the one slider node still carries the dial", (boolean) c[0]);
        check(name + ": a value set outside any draw updates the reading of the latest draw (\"" + c[1] + "\", a thumb at it reads \"" + c[2] + "\")",
                c[1].equals(c[2]));
        check(name + ": the dial is unstaged at the end of the section", Double.isNaN((double) r[3]));
    }

    /** Section 54: the hover. A list card, a door and the crumb under a still pointer keep their hover look (the crumb's tooltip) across a kept redraw, and lose it when the pointer goes. */
    static void sectorsHover(Game g, KeptBench bench, String plain) throws Exception {
        System.out.println("\n--- 54: the hover is kept (Sectors) ---");
        java.util.Map<String, Boolean> found = new java.util.LinkedHashMap<>();
        final String ops = KeptBench.sectorPages().get(0);
        KeptBench.Page list = fx(bench::sectors);
        arrive(bench, list);
        hoverLine(found, "the Sectors list", "sectorCard", fx(() -> bench.hoverKept("sectorCard", bench::showSectors)));
        KeptBench.Page business = fx(() -> bench.sectors(possessive(called(g, plain)) + " Operations page", plain, ops, false));
        arrive(bench, business);
        hoverLine(found, possessive(called(g, plain)) + " page", "door", fx(() -> bench.hoverKept("door", bench::showSectors)));
        hoverLine(found, possessive(called(g, plain)) + " page", "crumb", fx(() -> bench.hoverCrumb(bench::showSectors)));
        for (String kind : List.of("sectorCard", "door", "crumb")) {
            check("a " + kind + " was found on a Sectors page of this city, to put the pointer on", found.getOrDefault(kind, false));
        }
    }

    /* =====================================================================
       THE MONEY SCREENS (KP5), sections 55 to 68: the Bank tab, the Finances tab and the city's fund, each page drawn kept from the
       screen's pool and built new from an empty one - kept, shown as a page built new shows it, kept right through what reshapes
       it - then the pictures on them, the count-up the clock allows, the boxes a player types in, the hover, and what it costs.
       ===================================================================== */

    /**
     * The budget for the kept Bank month - its draw, then the pulse's CSS and layout - on the Bank's heaviest page (the Overview), on the PC: twice the
     * kept month's median, rounded up (5.2 to 8.3 ms in five runs of the money sections alone, in a city played to month 2,100 as KeptCheck's is when
     * it comes to them, 5.6 between them; the same month built new 15.9 to 19.2 ms).
     */
    static final double KEPT_BANK_MS = 12;

    /**
     * The budget for the kept Finances month on the heaviest of the two pages costed (Borrow at home, the page the auction draws, and the page with the
     * most nodes), on the PC: twice the kept month's median, rounded up (the same five runs: Borrow 7.6 to 10.2 ms, 8.6 between them, built new 16.7 to
     * 23.8; The book 7.2 to 9.0 ms, 8.6 between them, built new 16.0 to 21.9).
     */
    static final double KEPT_FINANCES_MS = 18;

    /**
     * The budget for the kept month on the fund's heaviest page (Search and Activity, the two costed, weigh most; their size follows the city's age: the
     * bonds it has issued are the rows of Search, its months of events the rows of Activity), on the PC: twice the kept month's median, rounded up
     * (the same five runs: Activity 124.3 to 191.9 ms, 160.3 between them, built new 195.1 to 252.7; Search 57.8 to 71.3, 62.0 between them, built new
     * 105.9 to 172.6). Played alone from month 605 they were a quarter of that; in KeptCheck's own city at month 2,100 Search had 13,000 nodes and
     * took 228 ms.
     */
    static final double KEPT_FUND_MS = 321;

    /** The Bank tab's pages that have two views, a Summary and a Statement. */
    private static boolean twoViews(String page) {
        return page.equals("Profit") || page.equals("Balance sheet");
    }

    /** The hand that opens a Bank page, in a view (the Overview when `page` is null). */
    static Hand bankOn(String page, boolean statement) {
        return b -> {
            b.bankOpen(page == null ? null : "pages", page, statement);
            return null;
        };
    }

    /** What the Bank's folds hold, by page: the fold's key, then the lines a player opens inside it (each a key the page's own bookLine opens by). */
    static final String[][] BANK_FOLDS = {
            {"Profit", "profit:statement", "Interest earned", "Fees", "Provisions for loans expected to go bad", "The trading desk", "Staff and branches"},
            {"Balance sheet", "sheet:statement", "sheet:BUSINESS_LOANS", "sheet:INTERIM", "Preferred shares (the city)", "Paid-in capital", "Retained earnings"},
            {"Lending", "lending:tables"}, {"Lending", "lending:bonds"}, {"Funding", "funding:lines"},
            {"Capital & owners", "capital:lines"}};

    /** Sections 55 to 57: the Bank tab kept. */
    static void bank(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The Bank tab (KP5) ===");
        // The clock paused: a month stepped by hand counts up at any speed, so the pages are drawn with their pops and the gauge's growth running
        // (and finished where a signature is read), the harder case for a page kept.
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        final String profit = "Profit", sheet = "Balance sheet";
        KeptBench.Page overview = fx(bench::bank);
        KeptBench.Page profitSummary = fx(() -> bench.bank("the Bank's Profit page", "pages", profit, false));
        KeptBench.Page profitStatement = fx(() -> bench.bank("the Bank's Profit page as a statement", "pages", profit, true));
        KeptBench.Page sheetStatement = fx(() -> bench.bank("the Bank's Balance sheet as a statement", "pages", sheet, true));
        pageKept(g, bench, overview, 55);
        pageKept(g, bench, profitSummary, 55);
        for (KeptBench.Page p : List.of(overview, profitSummary, profitStatement, sheetStatement)) {
            arrive(bench, p);
            pageShows(g, bench, p, 56);
        }

        // 57: what reshapes the Bank tab.
        KeptBench.Page tab = fx(bench::bank);
        List<Choice> pages = new ArrayList<>();
        pages.add(choice("the Overview", bankOn(null, false)));
        for (String p : KeptBench.bankPages()) {
            pages.add(choice(p + (twoViews(p) ? ", the Summary view" : ""), bankOn(p, false)));
            if (twoViews(p)) pages.add(choice(p + ", the Statement view", bankOn(p, true)));
        }
        pages.add(choice("the Overview again", bankOn(null, false)));
        pageReshaped(g, bench, tab, 57, pages);

        // A fold opened has to add to the page, and a line opened inside it; both closed take them away.
        List<Choice> folds = new ArrayList<>();
        for (String[] row : BANK_FOLDS) {
            String page = row[0], fold = row[1];
            folds.add(choice(page + " page", bankOn(page, false)));
            // (The Lending page's fold of the businesses' bonds it holds is empty in a bank that holds none: it adds nothing there, and is opened all the same.)
            folds.add(new Choice(page + ": the fold " + fold + " opened", b -> { b.bankLine(fold, true); return null; }, !fold.equals("lending:bonds")));
            if (row.length > 2) {
                // A statement line's detail is built hidden and shown when the line opens: no node is added, one is shown.
                folds.add(choice(page + ": every line inside it opened (the page changes)", changing(tab, b -> {
                    for (int i = 2; i < row.length; i++) b.bankLine(row[i], true);
                    return null;
                })));
                folds.add(choice(page + ": every line closed", b -> {
                    for (int i = 2; i < row.length; i++) b.bankLine(row[i], false);
                    return null;
                }));
            }
            folds.add(choice(page + ": the fold " + fold + " closed", b -> { b.bankLine(fold, false); return null; }));
        }
        pageReshaped(g, bench, tab, 57, folds);

        // The waterfall's year, on and off; the statement view's two columns, off and on.
        List<Choice> views = new ArrayList<>();
        views.add(choice("Profit page", bankOn(profit, false)));
        views.add(choice("the waterfall on the last twelve months", b -> { b.bankYear(true); return null; }));
        views.add(choice("the waterfall on this month again", b -> { b.bankYear(false); return null; }));
        views.add(choice("the Profit statement", bankOn(profit, true)));
        views.add(choice("the change column off", b -> { b.sectorsColumns(false, true); return null; }));
        views.add(choice("...the common-size column off too", b -> { b.sectorsColumns(false, false); return null; }));
        views.add(choice("...both on again", b -> { b.sectorsColumns(true, true); return null; }));
        views.add(choice("the Balance sheet statement", bankOn(sheet, true)));
        views.add(choice("...its change column off", b -> { b.sectorsColumns(false, true); return null; }));
        views.add(choice("...both on again", b -> { b.sectorsColumns(true, true); return null; }));
        pageReshaped(g, bench, tab, 57, views);
        // Foreign money in the bank (the Funding page's alert and its part of the bar): its hot money is rebuilt each month from the capital flows
        // (Bank.setForeignDeposits), so it is put there by hand, the page drawn kept and built new, and the month after, without. Printed, not asserted: in a
        // city of two thousand months the two pages differ in one hidden, empty label of the bar (13 px against 0), which nobody sees.
        String foreignSays = fx(() -> {
            bench.home(tab);
            bench.bankOpen("pages", "Funding", false);
            bench.drawKept(tab);
            bench.passes();
            g.getBank().setForeignDeposits(Math.max(1000, g.getBank().exposure() / 10));
            bench.drawKept(tab);
            String with = againstNew(bench, tab);
            month(g);
            bench.drawKept(tab);
            String after = againstNew(bench, tab);
            return "foreign money in the bank: the page drawn kept and built new " + (with == null ? "the same" : "apart (" + with.split("\n")[0] + ")")
                    + "; a month later " + (after == null ? "the same" : "apart (" + after.split("\n")[0] + ")");
        });
        System.out.println("  " + foreignSays);
        System.out.println("  not reached by a fixture, drawn by the same code as the pages around them: the Bank's rescue and the preferred offer (its action cards: they need a failed bank), a city with no bank (no branch standing)");
        bankPicks(g, bench);
    }

    /**
     * A hand that has to change the page (the premise of a line opened that adds no node: a statement line's detail is built hidden and
     * shown): the page drawn before and after, and the choice refused as made if the two are the same.
     */
    static Hand changing(KeptBench.Page page, Hand hand) {
        return b -> {
            b.drawKept(page);
            b.passes();
            b.settle();
            List<String> before = b.signature();
            String refused = hand.on(b);
            if (refused != null) return refused;
            b.drawKept(page);
            b.passes();
            b.settle();
            return before.equals(b.signature()) ? "the choice changed nothing on the page" : null;
        };
    }

    /**
     * Section 57's last part: a chip picked lands at the top of its page (BankScreen.toTop). A kept page keeps its scroller, so a
     * pick of the page already on show - whose scope, and scroller, are the same - has to put it back to the top; another page, or the
     * other view, is a scope of its own with a scroller of its own, which opens there. The pages are the Overview and the Profit page
     * with its statement opened: long enough that the scroller has somewhere to go (the premise, asserted).
     */
    static void bankPicks(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 57: a chip picked lands at the top of its page ---");
        KeptBench.Page overview = fx(bench::bank);
        KeptBench.Page profit = fx(() -> bench.bank("the Bank's Profit page", "pages", "Profit", false));
        for (KeptBench.Page page : List.of(overview, profit)) {
            final String chip = page == overview ? "Overview" : "Profit";
            Object[] r = fx(() -> {
                bench.home(page);
                if (page == profit) bench.bankLine("profit:statement", true);
                bench.showBank();
                bench.passes();
                bench.settle();
                javafx.scene.control.ScrollPane sp = bench.pageScroller();
                if (sp == null) return null;
                double room = sp.getContent().getLayoutBounds().getHeight() - sp.getViewportBounds().getHeight();
                sp.setVvalue(.6);
                bench.passes();
                double put = sp.getVvalue();
                // A month lands: the kept scroller stays where the player left it.
                month(g);
                bench.showBank();
                bench.passes();
                // (The scroller keeps the pixel it is at when the content grows or shrinks by a card, so its fraction moves: a reset would be 0.)
                boolean stayed = bench.pageScroller() == sp && sp.getVvalue() > 0;
                double later = bench.pageScroller() == null ? Double.NaN : bench.pageScroller().getVvalue();
                // The page's own chip, picked.
                bench.bankPick(chip);
                bench.passes();
                boolean ownSame = bench.pageScroller() == sp;
                double ownAt = sp.getVvalue();
                String ownDiffers = againstNew(bench, page);
                return new Object[]{room, put, stayed, ownSame, ownAt, ownDiffers, later};
            });
            if (r == null) {
                check("the Bank's " + page.name() + " has a scroller", false);
                continue;
            }
            System.out.printf("  %s: %.0f px to scroll; scrolled to %.6f; a month later it stayed: %s (at %.6f); its own chip: %.2f (the same scroller: %s)%n",
                    page.name(), r[0], r[1], r[2], r[6], r[4], r[3]);
            check(page.name() + " is longer than its window (" + String.format("%.0f", (double) r[0]) + " px to scroll), and its scroller took the player's value (" + r[1] + ")",
                    (double) r[0] > 0 && (double) r[1] > 0);
            check("...a month landing leaves the kept scroller where the player left it", (boolean) r[2]);
            check("...its own chip picked leaves the same scroller (the page is kept) at the top (" + r[4] + ")", (boolean) r[3] && (double) r[4] == 0);
            check("...and the page is equal to a page built new", r[5] == null);
            if (r[5] != null) System.out.println("  " + r[5]);
        }
        // The Overview's chip from a scrolled Profit page, and the switch to the other view from a scrolled page: each another scope.
        Object[] s = fx(() -> {
            bench.home(profit);
            bench.bankLine("profit:statement", true);
            bench.showBank();
            bench.passes();
            bench.pageScroller().setVvalue(.6);
            bench.bankPick("Overview");
            bench.passes();
            double overviewAt = bench.pageScroller().getVvalue();
            bench.bankPick("Profit");
            bench.passes();
            bench.pageScroller().setVvalue(.6);
            bench.bankPickView(true);
            bench.passes();
            double statementAt = bench.pageScroller().getVvalue();
            bench.pageScroller().setVvalue(.6);
            bench.bankPickView(false);
            bench.passes();
            double summaryAt = bench.pageScroller().getVvalue();
            bench.home(profit);
            return new Object[]{overviewAt, statementAt, summaryAt};
        });
        check("the Overview's chip picked from a scrolled Profit page lands at its top (" + s[0] + ")", (double) s[0] == 0);
        check("the Statement view picked from a scrolled page lands at its top (" + s[1] + ")", (double) s[1] == 0);
        check("...and the Summary view picked again, scrolled, at its top (" + s[2] + ")", (double) s[2] == 0);
    }

    /* ----- Finances ----- */

    /** The paper the money sections sell, one of each kind the Borrow page offers (and a callable bond too): {kind, term, "callable" or ""}. The Note first: it falls due soonest. */
    static final String[][] PAPER = {{"Note", "12", ""}, {"Float", "5", ""}, {"Serial", "10", ""}, {"Term", "10", ""}, {"Term", "20", "callable"}, {"Linked", "10", ""}};

    /** A piece of paper in words: its kind, as the book writes it, and its maturity. */
    static String pieceWords(Debt d) {
        return d.getClass().getSimpleName() + (d instanceof LongTermBond b && b.isCallable() ? " (callable)" : "") + " due month " + d.getMaturityMonth();
    }

    /**
     * The city's books put where the Finances screens have something to draw: one piece of every kind of paper sold through the
     * model, as the Borrow page's own button sells it (executeDebtLogic, at the default highest yield), and a month played. Returns
     * the pieces that came of it, in PAPER's order (null for a sale the auction would not make), after printing what each sale said.
     */
    static List<Debt> sellPaper(Game g, KeptBench bench) throws Exception {
        List<Debt> out = new ArrayList<>();
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 3, g.getCash() / 40));
        for (String[] p : PAPER) {
            List<Debt> before = fx(bench::financesPieces);
            String said = fx(() -> quietly(() -> bench.financesSell(p[0], Integer.parseInt(p[1]), ask, !p[2].isEmpty())));
            List<Debt> after = fx(bench::financesPieces);
            after.removeAll(before);
            out.add(after.isEmpty() ? null : after.get(0));
            System.out.println("  sold " + p[0] + " " + p[1] + (p[2].isEmpty() ? "" : " " + p[2]) + ": "
                    + (after.isEmpty() ? "nothing came of it (" + said.split("\n")[0] + ")" : pieceWords(after.get(0))));
        }
        fx(() -> {
            month(g);
            return null;
        });
        return out;
    }

    /** The hand that puts the Finances tab on an area and a page. */
    static Hand financesOn(String area, String page) {
        return b -> {
            b.financesOpen(area, page);
            return null;
        };
    }

    /** The hand that opens a piece's own page, and the piece still on the books is its premise. */
    static Hand lineOf(Debt piece) {
        return b -> {
            if (!b.financesPieces().contains(piece)) return "the piece has fallen due";
            b.financesLine(piece);
            return null;
        };
    }

    /** The hand that opens Close it now on a piece (from its line's page when that is open), the same premise. */
    static Hand closeOf(Debt piece) {
        return b -> {
            if (!b.financesPieces().contains(piece)) return "the piece has fallen due";
            b.financesClose(piece);
            return null;
        };
    }

    /** Sections 58 to 60: the Finances tab kept. */
    static void finances(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The Finances tab (KP5) ===");
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        KeptBench.Page hub = fx(bench::finances);
        KeptBench.Page position = fx(() -> bench.finances("the position's Overview", "The position", "Overview"));
        pageKept(g, bench, hub, 58);
        pageKept(g, bench, position, 58);
        for (KeptBench.Page p : List.of(hub, position)) {
            arrive(bench, p);
            pageShows(g, bench, p, 59);
        }

        // 60: what reshapes the tab. Every area and page first (the fund's are section 63's).
        KeptBench.Page tab = fx(bench::finances);
        List<Choice> pages = new ArrayList<>();
        pages.add(choice("the hub", financesOn(null, null)));
        for (String area : KeptBench.financeAreas()) {
            if (area.equals("The city's fund")) continue;
            for (String page : KeptBench.financePages(area)) {
                if (page.equals("A bond's book")) continue;
                pages.add(choice(area + ", " + page, financesOn(area, page)));
            }
        }
        pages.add(choice("the hub again", financesOn(null, null)));
        pageReshaped(g, bench, tab, 60, pages);

        // The bond market: its folds opened (each has to add to the page), and the book of a bond, two of the largest.
        List<Integer> bonds = fx(bench::bondIdsLasting);
        System.out.println("  " + bonds.size() + " businesses' bonds outstanding; the book of the two that fall due last (they outlast the section), and the fold of every issue as tables");
        List<Choice> market = new ArrayList<>();
        market.add(choice("The bond market, every issue", financesOn("The bond market", "Every issue")));
        market.add(new Choice("every bond as tables, the fold opened", b -> { b.financesFold("bonds:grids", true); return null; }, true));
        market.add(choice("...and closed", b -> { b.financesFold("bonds:grids", false); return null; }));
        for (int i = 0; i < Math.min(2, bonds.size()); i++) {
            final int id = bonds.get(i);
            market.add(choice("bond " + id + "'s book", b -> { b.financesBondBook(id); return null; }));
            market.add(new Choice("bond " + id + "'s book, its levels opened", b -> { b.financesFold("bond:levels", true); return null; }, true));
            market.add(choice("...and closed", b -> { b.financesFold("bond:levels", false); return null; }));
        }
        pageReshaped(g, bench, tab, 60, market);

        // Borrow: every kind of paper, its term, the ask, callable, the highest yield, what is typed; and abroad, every money.
        pageReshaped(g, bench, tab, 60, borrowChoices(g, bench));

        // The book: the pieces of paper sold through the model (a line's page and Close it now, each from each).
        System.out.println("\n--- 60: the book, a line's page and Close it now ---");
        List<Debt> sold = sellPaper(g, bench);
        List<Choice> book = new ArrayList<>();
        book.add(choice("The book", b -> { b.financesLine(null); return null; }));
        for (int i = 0; i < PAPER.length; i++) {
            Debt piece = sold.get(i);
            if (piece == null) continue;
            String what = pieceWords(piece);
            book.add(choice("the line's page of " + what, lineOf(piece)));
            book.add(choice("Close it now on " + what + " (from its line's page)", closeOf(piece)));
            book.add(choice("...the line's page again", lineOf(piece)));
            book.add(choice("The book again", b -> { b.financesLine(null); return null; }));
            book.add(choice("Close it now on " + what + " (from the book)", closeOf(piece)));
            if (i == 3) {
                // The ticket as typed: a face, a price, words it cannot read.
                book.add(choice("...the face typed, 1,000,000", b -> { b.financesTyped(1, "1,000,000"); return null; }));
                book.add(choice("...and the most to pay, 98.5", b -> { b.financesTyped(2, "98.5"); return null; }));
                book.add(choice("...a face it cannot read", b -> { b.financesTyped(1, "bogus"); return null; }));
                book.add(choice("...a price it cannot read", b -> { b.financesTyped(2, "x"); return null; }));
                book.add(choice("...the boxes emptied", b -> { b.financesTyped(1, ""); b.financesTyped(2, ""); return null; }));
            }
        }
        pageReshaped(g, bench, tab, 60, book);
    }

    /** Borrow's choices: the kinds, their terms, the ask, callable, the highest yield, what is typed, and abroad every money. */
    static List<Choice> borrowChoices(Game g, KeptBench bench) throws Exception {
        List<String> moneys = KeptBench.borrowMoneys();
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 4, g.getCash() / 6));
        List<Choice> out = new ArrayList<>();
        for (String kind : KeptBench.borrowKinds()) {
            List<Integer> terms = KeptBench.borrowTerms(kind);
            for (int term : kind.equals("Term") ? new int[]{terms.get(0), terms.get(terms.size() - 1)} : new int[]{terms.get(terms.size() / 2)}) {
                for (double a : kind.equals("Term") ? new double[]{0, ask, ask * 40} : new double[]{0, ask}) {
                    String tag = kind + " " + term + " asking " + (a == 0 ? "nothing" : a == ask ? "a sixth of the cash" : "forty times that");
                    out.add(choice("Borrow at home: " + tag, b -> {
                        b.financesOpen("Borrow", "At home");
                        b.financesBorrow(kind, term, moneys.get(0), a, false, false);
                        return null;
                    }));
                }
            }
        }
        out.add(choice("Borrow at home: asking for half the minimum issue (under it)", b -> {
            b.financesOpen("Borrow", "At home");
            b.financesBorrow("Term", 10, moneys.get(0), g.minimumIssueSize() / 2, false, false);
            return null;
        }));
        out.add(choice("Borrow at home: a bond asked callable", b -> {
            b.financesOpen("Borrow", "At home");
            b.financesBorrow("Term", 20, moneys.get(0), ask, true, false);
            return null;
        }));
        for (boolean fill : new boolean[]{false, true}) {
            out.add(choice("Borrow at home: the highest yield " + (fill ? "fill it" : "9%"), b -> {
                b.financesOpen("Borrow", "At home");
                b.financesBorrow("Term", 10, moneys.get(0), ask, false, false);
                b.financesCap(fill, .09);
                return null;
            }));
        }
        out.add(choice("Borrow at home: the highest yield back to fair + the tail", b -> { b.financesCapDefault(); return null; }));
        for (String typed : new String[]{"40B", "bogus", ""}) {
            out.add(choice("Borrow at home: typed \"" + typed + "\" in the ask's box", b -> {
                b.financesOpen("Borrow", "At home");
                b.financesBorrow("Term", 10, moneys.get(0), typed.equals("40B") ? ask : 0, false, false);
                b.financesAskTyped(typed.isEmpty() ? null : typed, typed.equals("bogus") ? "“bogus” is not an amount" : null);
                return null;
            }));
        }
        for (String money : moneys) {
            for (boolean hold : new boolean[]{false, true}) {
                if (hold && !money.equals(moneys.get(0)) && !money.equals(moneys.get(moneys.size() - 1))) continue;
                out.add(choice("Borrow abroad: a bond in " + money + (hold ? ", the dollars held" : ""), b -> {
                    b.financesOpen("Borrow", "Abroad");
                    b.financesBorrow("Term", 10, money, ask, false, hold);
                    return null;
                }));
            }
        }
        out.add(choice("Borrow abroad: asking for a kind that is home's only falls back to a bond", b -> {
            b.financesOpen("Borrow", "Abroad");
            b.financesBorrow("Serial", 10, moneys.get(0), ask, false, false);
            return null;
        }));
        return out;
    }

    /** Sections 60's last part: the foreign default menu and the debt result menu, each an arrival and a month later. */
    static void financesMenus(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 60: the foreign default menu and the debt result menu ---");
        List<String> moneys = KeptBench.borrowMoneys();
        KeptBench.Page fd = fx(bench::foreignDefault);
        pageReshaped(g, bench, fd, 60, List.of(choice("the foreign default menu with nothing owed abroad opened", b -> null)));
        double abroad = fx(() -> Math.max(g.minimumIssueSizeIn(moneys.get(0)) * 3, 1_000_000));
        String said = fx(() -> quietly(() -> bench.financesBook("Term", 10, abroad, true, moneys.get(0))));
        System.out.println("  borrowed abroad through the model: " + said.split("\n")[0]);
        pageReshaped(g, bench, fd, 60, List.of(choice("the foreign default menu with debt abroad opened", b -> null)));
        KeptBench.Page dr = fx(bench::debtResult);
        pageReshaped(g, bench, dr, 60, List.of(choice("the debt result menu (paper booked abroad) opened", b -> null)));
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 3, g.getCash() / 40));
        for (String[] k : new String[][]{{"Note", "12"}, {"Term", "10"}, {"Linked", "10"}}) {
            String got = fx(() -> quietly(() -> bench.financesBook(k[0], Integer.parseInt(k[1]), ask, false, null)));
            System.out.println("  booked " + k[0] + " at home: " + got.split("\n")[0]);
            pageReshaped(g, bench, dr, 60, List.of(choice("the debt result menu (" + k[0] + " booked at home) opened", b -> null)));
        }
        fx(() -> {
            bench.financesBookedNothing("Nothing sold: the bids do not reach it.");
            return null;
        });
        pageReshaped(g, bench, dr, 60, List.of(choice("the debt result menu (nothing issued, the booking's words) opened", b -> null)));
        fx(() -> {
            bench.financesBookedNothing(null);
            return null;
        });
        pageReshaped(g, bench, dr, 60, List.of(choice("the debt result menu (nothing issued, no words) opened", b -> null)));
    }

    /* ----- the city's fund ----- */

    /** The fund's pages by name (Portfolio, Search, Activity, Rules & cash). */
    static Hand fundOn(String page) {
        return b -> {
            b.fundPage(page);
            return null;
        };
    }

    /** The hand that opens a security's page by its lot key, from a page of the fund. */
    static Hand securityOf(String key, String from) {
        return b -> {
            b.fundOpen(key, from);
            return null;
        };
    }

    /** The hand that sets the ticket on the security open. */
    static Hand ticket(boolean buy, boolean byAmount, double amount, String kind, int steps, boolean review) {
        return b -> {
            b.fundTicket(buy, byAmount, amount, kind, steps, review);
            return null;
        };
    }

    /** The first lot key in `from` that starts with `prefix` and is not in `not` (and has no ":rescue" in it unless `rescue`), or null. */
    static String lotOf(List<String> from, String prefix, List<String> not, boolean rescue) {
        for (String key : from) if (key.startsWith(prefix) && !not.contains(key) && key.contains(":rescue") == rescue) return key;
        return null;
    }

    /** Sections 61 to 63: the city's fund kept. */
    static void fund(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The city's fund (KP5) ===");
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        List<String> held = fx(bench::fundHoldings);
        if (held.isEmpty()) {
            // The fund has nothing: paid in, and told to buy through the model as the ticket's button does.
            fx(() -> quietly(() -> {
                g.fundPayIn(Math.max(0, g.getCash() * .5));
                double each = g.fundCashFree() / 6;
                for (int c : new int[]{0, 1, 2, 3}) g.fundBuyShares(c, each, 0);
                List<CorporateBond> bonds = new ArrayList<>(g.getBondMarket().getBonds());
                for (int i = 0; i < Math.min(2, bonds.size()); i++) g.fundBuyBond(bonds.get(i).id(), each, 0);
                return null;
            }));
            fx(() -> {
                for (int i = 0; i < 3; i++) month(g);
                return null;
            });
            held = fx(bench::fundHoldings);
        }
        final List<String> holdings = held;
        List<String> securities = fx(bench::fundSecurities);
        System.out.println("  the fund holds " + holdings.size() + " lots; " + securities.size() + " securities in its reach");
        check("the fund holds something to draw (" + holdings.size() + " lots)", !holdings.isEmpty());
        // The month landing on the tab, as the real show method tells the fund (only then does the worth count on and a payment pop).
        fx(() -> {
            bench.fundOnShow(true);
            return null;
        });
        KeptBench.Page portfolio = fx(() -> bench.finances("the fund's Portfolio", "The city's fund", "Portfolio"));
        long t = System.nanoTime();
        pageKept(g, bench, portfolio, 61, true);
        arrive(bench, portfolio);
        pageShows(g, bench, portfolio, 62);
        t = took("Portfolio kept and shown", t);

        // 63: what reshapes the fund.
        KeptBench.Page tab = fx(() -> bench.finances("the city's fund", "The city's fund", "Portfolio"));
        List<Choice> pages = new ArrayList<>();
        for (String page : KeptBench.financePages("The city's fund")) {
            // (Activity's rows are the month's events: see the Activity choices below.)
            pages.add(page.equals("Activity") ? acting("the fund's " + page, fundOn(page)) : choice("the fund's " + page, fundOn(page)));
        }
        pages.add(choice("the fund's Portfolio again", fundOn("Portfolio")));
        // The folds and the rows that open: a bond issuer's, "and N more", the closed lots, the paid-in fold, an activity line that groups.
        pages.add(new Choice("every row that folds opened on Portfolio", b -> {
            b.fundPage("Portfolio");
            return b.fundUnfold() > 0 ? null : "the Portfolio has no row that folds";
        }, true));
        pages.add(choice("...and all of them closed", b -> { b.fundFoldsClosed(); return null; }));
        pages.add(choice("the closed lots' fold opened (the page changes if the fund has closed lots)", b -> { b.fundFold("fund:closed", true); return null; }));
        pages.add(choice("...and closed", b -> { b.fundFold("fund:closed", false); return null; }));
        pages.add(choice("Rules & cash", fundOn("Rules & cash")));
        pages.add(new Choice("the paid-in fold opened", b -> { b.fundFold("fund:life", true); return null; }, true));
        pages.add(choice("...and closed", b -> { b.fundFold("fund:life", false); return null; }));
        for (double ask : new double[]{1_000_000, 50_000_000_000.0, 0}) {
            pages.add(choice("PAY IN, DRAW OUT asking " + ask, b -> { b.fundAsk(ask); return null; }));
        }
        pageReshaped(g, bench, tab, 63, pages);
        t = took("the fund's pages and folds", t);

        // Search: the words and the chips; Activity: the groups, the months, the lines that group.
        List<Choice> search = new ArrayList<>();
        // (A bond named by its number, one row, on each chip; and the whole list once: the whole list is 8,000 nodes and 19,000 pixels tall, a stress
        // for the page's scroller more than for the nodes - a word that names a company would list its dozens of bonds, a different page in a different city.)
        String oneBond = lotOf(securities, "B:", List.of(), false);
        String word = oneBond == null ? "bond" : "#" + oneBond.substring(2);
        for (String filter : KeptBench.fundFilters()) {
            search.add(choice("Search \"" + word + "\" on the " + filter + " chip", b -> {
                b.fundPage("Search");
                b.fundSearch(word, filter);
                return null;
            }));
        }
        search.add(choice("Search \"zzzzqq\", which nothing matches", b -> { b.fundSearch("zzzzqq", "All"); return null; }));
        search.add(choice("Search put back", b -> { b.fundSearch("", "All"); return null; }));
        // Activity's rows are keyed by their words, and a line of the record names the bond it is about as the book names it now: in the month a
        // bond with a long record is repaid, every line of it reads other words and is made new (59 rows, 611 nodes of 2,700 in this city at month 615).
        // That is the page showing the month's events, not a page rebuilt, so the share of its nodes a month hands back is printed there and not asserted.
        for (String group : List.of("Trades", "All")) {
            for (int months : group.equals("All") ? new int[]{6} : new int[]{60}) {
                search.add(acting("Activity, " + group + ", " + months + " months", b -> {
                    b.fundPage("Activity");
                    b.fundActivity(group, months);
                    return null;
                }));
            }
        }
        search.add(acting("Activity's grouped lines opened (the rule's trades of a month, when it made some)", b -> {
            b.fundPage("Activity");
            b.fundUnfold();
            return null;
        }));
        search.add(acting("...and closed", b -> { b.fundFoldsClosed(); return null; }));
        pageReshaped(g, bench, tab, 63, search);
        t = took("Search and Activity", t);

        // A security's page, from Portfolio and from Search, with the ticket's choices.
        List<String> none = List.of();
        String share = lotOf(holdings, "S:", none, false), bond = lotOf(holdings, "B:", none, false), rescue = lotOf(holdings, "S:", none, true);
        String freeShare = lotOf(securities, "S:", holdings, false), freeBond = lotOf(securities, "B:", holdings, false);
        System.out.println("  securities drawn: a share held " + share + ", a bond held " + bond + ", the rescue book " + rescue
                + ", a share not held " + freeShare + ", a bond not held " + freeBond);
        List<Choice> security = new ArrayList<>();
        for (String key : new String[]{share, bond, rescue, freeShare, freeBond}) {
            if (key == null) continue;
            // (A security's page lists its own record, a dozen lines of the month's events: the nodes a month makes there are the new lines.)
            security.add(acting("the page of " + key + " from Portfolio", securityOf(key, "Portfolio")));
            security.add(acting("the page of " + key + " from Search", securityOf(key, "Search")));
        }
        // The ticket's choices on a share held (buy and sell, by amount and by quantity, the kinds of price, the review) and, fewer, on a bond not held.
        for (String key : new String[]{share, freeBond}) {
            if (key == null) continue;
            security.add(acting("the page of " + key + " (the ticket fresh)", securityOf(key, "Portfolio")));
            for (boolean buy : new boolean[]{true, false}) {
                for (boolean byAmount : new boolean[]{true, false}) {
                    for (String kind : KeptBench.fundPriceKinds()) {
                        if (!kind.equals("fair") && !kind.equals("own") && !(kind.equals("bid") && buy && byAmount)) continue;
                        if (!byAmount && !kind.equals("fair")) continue;
                        if (key == freeBond && (!byAmount || !kind.equals("fair"))) continue;
                        for (boolean review : new boolean[]{false, true}) {
                            if (review && kind.equals("last")) continue;
                            security.add(choice(key + ": " + (buy ? "buy" : "sell") + (byAmount ? " by amount at " : " by quantity at ") + kind
                                    + (review ? ", at its review" : ""), ticket(buy, byAmount, byAmount ? 25_000 : 12, kind, kind.equals("own") ? 3 : 0, review)));
                        }
                    }
                }
            }
        }
        pageReshaped(g, bench, tab, 63, security);
        took("the securities", t);
        fx(() -> {
            bench.fundOnShow(false);
            bench.fundPage("Portfolio");
            return null;
        });
    }

    /* ----- the pictures (64) ----- */

    /**
     * A place a picture is seen: its label, the page, the hand that sets the page for it after the page's home, and what
     * the player does on the page in each month it is watched, after the month and before the draw (null: nothing).
     */
    record Spot(String label, KeptBench.Page page, Hand setup, MonthHand month) {
        Spot(String label, KeptBench.Page page, Hand setup) {
            this(label, page, setup, null);
        }
    }

    /** What the player does on a page in a month of the watch (the first month is 1). */
    interface MonthHand {
        void on(KeptBench bench, int month) throws Exception;
    }

    /** The most to pay typed on Close it now in the odd months of the watch, by turns (Spot's month hand). */
    static final String[] MOST_TO_PAY = { "98.5", "101.5" };

    /** The piece of paper on the books that falls due last: the one a line's page and Close it now are drawn on (null when the city has none). */
    static Debt longestPiece(KeptBench bench) throws Exception {
        Debt best = null;
        for (Debt d : fx(bench::financesPieces)) if (best == null || d.getMaturityMonth() > best.getMaturityMonth()) best = d;
        return best;
    }

    /** The places the money screens' pictures are: one page of each tab that holds some, and the pages behind them. */
    static List<Spot> pictureSpots(Game g, KeptBench bench, Debt piece) throws Exception {
        List<String> moneys = KeptBench.borrowMoneys();
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 4, g.getCash() / 6));
        List<Spot> spots = new ArrayList<>();
        spots.add(new Spot("the Bank Overview", fx(bench::bank), b -> null));
        for (String page : List.of("Profit", "Balance sheet", "Lending", "Funding", "Capital & owners", "History")) {
            spots.add(new Spot("the Bank's " + page + " page", fx(() -> bench.bank("the Bank's " + page + " page", "pages", page, false)), b -> null));
        }
        spots.add(new Spot("the Finances hub", fx(bench::finances), b -> null));
        for (String page : KeptBench.financePages("The position")) {
            spots.add(new Spot("the position's " + page + " page", fx(() -> bench.finances("the position's " + page + " page", "The position", page)), b -> null));
        }
        spots.add(new Spot("The book", fx(() -> bench.finances("The book", "The book", "Every piece")), b -> null));
        if (piece != null) {
            spots.add(new Spot("a line's page", fx(() -> bench.finances("a line's page", "The book", "Every piece")), lineOf(piece)));
            // The ladder's offers and fair price can stand still for months (R1c's city: six, with not a figure of them moved), so the
            // player types a most to pay in the odd months and leaves it in the even ones: months that move what it draws, and months
            // that move only what it does not (the books' carrying value of the face taken), which must draw nothing.
            spots.add(new Spot("Close it now", fx(() -> bench.finances("Close it now", "The book", "Every piece")), closeOf(piece),
                    (b, m) -> { if (m % 2 == 1) b.financesTyped(2, MOST_TO_PAY[m / 2 % 2]); }));
        }
        spots.add(new Spot("Borrow at home, asking for a sixth of the cash", fx(() -> bench.finances("Borrow at home", "Borrow", "At home")), b -> {
            b.financesBorrow("Term", 10, moneys.get(0), ask, false, false);
            return null;
        }));
        spots.add(new Spot("Money", fx(() -> bench.finances("Money", "Money", "Money")), b -> null));
        spots.add(new Spot("the bond market", fx(() -> bench.finances("the bond market", "The bond market", "Every issue")), b -> null));
        List<Integer> bonds = fx(bench::bondIdsLasting);
        if (!bonds.isEmpty()) {
            spots.add(new Spot("a bond's book", fx(() -> bench.finances("a bond's book", "The bond market", "A bond's book")), b -> {
                b.financesBondBook(bonds.get(0));
                return null;
            }));
        }
        spots.add(new Spot("the fund's Portfolio", fx(() -> bench.finances("the fund's Portfolio", "The city's fund", "Portfolio")), b -> null));
        List<String> held = fx(bench::fundHoldings);
        String share = lotOf(held, "S:", List.of(), false);
        if (share != null) {
            spots.add(new Spot("a security's page", fx(() -> bench.finances("a security's page", "The city's fund", "Portfolio")), securityOf(share, "Portfolio")));
        }
        return spots;
    }

    /**
     * Section 64: the pictures the money screens hold are kept. Each kind is found on the pages of this city (the census, printed), and on
     * each of the first two it is found on: it stays the node it was from month to month (nine in ten handed back), draws itself again only
     * where the month's figures moved (never when what it draws is the same), draws nothing on a second redraw in the same month, and the page
     * is equal to a page built new at every month.
     */
    static void moneyPictures(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 64: the pictures on the money screens are kept ---");
        Debt piece = longestPiece(bench);
        List<Spot> spots = pictureSpots(g, bench, piece);
        java.util.Map<String, List<Spot>> seen = new java.util.LinkedHashMap<>();
        for (Spot s : spots) {
            java.util.Map<String, Integer> found = fx(() -> {
                bench.home(s.page());
                s.setup().on(bench);
                bench.drawKept(s.page());
                bench.passes();
                bench.settle();
                java.util.Map<String, Integer> m = new java.util.LinkedHashMap<>();
                for (String kind : KeptBench.pictureKinds()) {
                    int c = bench.pictures(kind).size();
                    if (c > 0) m.put(kind, c);
                }
                return m;
            });
            System.out.println("  " + s.label() + ": " + (found.isEmpty() ? "no picture" : found.toString()));
            for (String kind : found.keySet()) seen.computeIfAbsent(kind, k -> new ArrayList<>()).add(s);
        }
        for (String kind : KeptBench.pictureKinds()) {
            List<Spot> at = seen.getOrDefault(kind, List.of());
            check("a " + kind + " is on a page of this city (" + at.size() + " pages)", !at.isEmpty());
            for (int i = 0; i < Math.min(2, at.size()); i++) pictureKept(g, bench, at.get(i), kind);
        }
    }

    /** One kind of picture on one page, watched for PICTURE_MONTHS months. */
    static void pictureKept(Game g, KeptBench bench, Spot spot, String kind) throws Exception {
        final KeptBench.Page page = spot.page();
        final boolean bar = kind.equals("RankBar"), chart = kind.equals("TimeChart");
        String what = "the " + kind + " on " + spot.label();
        int count = fx(() -> {
            bench.home(page);
            spot.setup().on(bench);
            bench.drawKept(page);
            bench.passes();
            bench.settle();
            // The bench counts a picture's clearings from the first time it is asked.
            for (Pane p : bench.pictures(kind)) bench.repaints(p);
            return bench.pictures(kind).size();
        });
        int repainted = 0, wasted = 0, quiet = 0, differ = 0;
        long kept = 0, had = 0;
        String firstDiffer = null, whyLoud = null;
        for (int m = 1; m <= PICTURE_MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                java.util.Map<Pane, Object[]> before = new java.util.IdentityHashMap<>();
                java.util.Map<Pane, Boolean> dirtyBefore = new java.util.IdentityHashMap<>();
                for (Pane p : bench.pictures(kind)) {
                    before.put(p, new Object[]{bench.repaints(p), bar ? KeptBench.figuresOf(p) : KeptBench.pictureSignature(p)});
                    dirtyBefore.put(p, KeptBench.layoutRequested(p));
                }
                month(g);
                if (spot.month() != null) spot.month().on(bench, at);
                bench.drawKept(page);
                java.util.Map<Pane, Boolean> asked = new java.util.IdentityHashMap<>();
                // (A bar asks for a layout when the draw makes it dirty: one that was dirty already - in a part of the page nobody lays out - asks nothing new.)
                for (Pane p : bench.pictures(kind)) asked.put(p, KeptBench.layoutRequested(p) && !dirtyBefore.getOrDefault(p, false));
                bench.passes();
                bench.finishPageAnimations();
                bench.settle();
                List<Pane> now = bench.pictures(kind);
                int held = 0, drew = 0, waste = 0;
                java.util.Map<Pane, Integer> landed = new java.util.IdentityHashMap<>();
                java.util.Map<Pane, String> landedFigures = new java.util.IdentityHashMap<>();
                java.util.Map<Pane, Boolean> dirtyLanded = new java.util.IdentityHashMap<>();
                for (Pane p : now) {
                    landed.put(p, bench.repaints(p));
                    landedFigures.put(p, KeptBench.figuresOf(p));
                    dirtyLanded.put(p, KeptBench.layoutRequested(p));
                    Object[] was = before.get(p);
                    if (was == null) continue;
                    held++;
                    boolean painted = bar ? asked.get(p) : bench.repaints(p) > (int) was[0];
                    if (painted) drew++;
                    if (painted && (bar ? KeptBench.figuresOf(p) : KeptBench.pictureSignature(p)).equals(was[1])) waste++;
                }
                // The same month drawn again: the figures are the same, so nothing is drawn.
                bench.drawKept(page);
                boolean still = true;
                String why = "";
                List<Pane> again = bench.pictures(kind);
                if (!again.equals(now)) {
                    still = false;
                    why = "the pictures on the page are other nodes (" + now.size() + " then " + again.size() + ")";
                }
                for (Pane p : again) {
                    // A chart is told its data and its size at every draw and paints a frame for each (TimeChart, since KP1); the other pictures draw nothing.
                    boolean quietOne = bar ? !KeptBench.layoutRequested(p) || dirtyLanded.getOrDefault(p, false)
                            : landed.containsKey(p) && bench.repaints(p) - landed.get(p) <= (chart ? 2 : 0);
                    if (!quietOne && still) why = bar ? "a bar asked to be laid out again (" + KeptBench.layoutReason(p) + "; the first draw told " + landedFigures.get(p) + ")" : "a picture drew again (" + landed.get(p) + " then " + bench.repaints(p) + ")";
                    still &= quietOne;
                }
                bench.passes();
                String d = againstNew(bench, page);
                return new Object[]{held, before.size(), drew, waste, still, d, why};
            });
            kept += (int) r[0];
            had += (int) r[1];
            if ((int) r[2] > 0) repainted++;
            if ((int) r[3] > 0) wasted++;
            if ((boolean) r[4]) quiet++;
            else if (whyLoud == null) whyLoud = "month " + g.getMonth() + ", " + r[6];
            if (r[5] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[5];
            }
        }
        System.out.printf("  %s: %d on the page; %d of %d months drew it again, %d of them with nothing changed; %d of %d pictures handed back%n",
                what, count, repainted, PICTURE_MONTHS, wasted, kept, had);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        if (whyLoud != null) System.out.println("  a redraw in the same month was not quiet: " + whyLoud);
        check(what + ": each month's redraw hands back nine in ten of the pictures it had, or more (" + kept + " of " + had + ")", kept * 10 >= had * 9);
        check(what + ": a month whose figures moved draws it again (" + repainted + " of " + PICTURE_MONTHS + " months did)", repainted > 0);
        check(what + ": no month draws it again with nothing changed in it (" + wasted + ")", wasted == 0);
        check(what + (chart ? ": a redraw in the same month paints no more than the two frames a chart is told (its data, its size) (" : ": a redraw in the same month draws nothing (")
                + quiet + " of " + PICTURE_MONTHS + ")", quiet == PICTURE_MONTHS);
        check(what + ": the page equals a page built new at every month (" + differ + " differ)", differ == 0);
    }

    /* ----- the count-up (65) ----- */

    /** The animations a draw has started since `before` that are running now: {pops, counts and growths, others}. */
    static int[] started(KeptBench bench, java.util.Set<Animation> before) {
        int pops = 0, grows = 0, other = 0;
        for (Animation a : bench.pageAnimations()) {
            if (before.contains(a) || a.getStatus() == Animation.Status.STOPPED) continue;
            if (a instanceof javafx.animation.ScaleTransition) pops++;
            else if (a instanceof javafx.animation.Timeline) grows++;
            else other++;
        }
        return new int[]{pops, grows, other};
    }

    /** The animations on the page now, as a set by identity. */
    static java.util.Set<Animation> animationsNow(KeptBench bench) {
        java.util.Set<Animation> set = Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        set.addAll(bench.pageAnimations());
        return set;
    }

    /** How many animations of a kind run on the page now. */
    static int running(KeptBench bench, Class<? extends Animation> kind) {
        int n = 0;
        for (Animation a : bench.pageAnimations()) if (kind.isInstance(a) && a.getStatus() != Animation.Status.STOPPED) n++;
        return n;
    }

    /** What is wrong with a page on which nothing may animate: a running animation, a figure not at rest. Empty when nothing is. */
    static String stillOrNot(KeptBench bench) {
        StringBuilder bad = new StringBuilder();
        if (bench.animating()) bad.append("an animation is still running; ");
        int off = bench.notAtRest();
        if (off > 0) bad.append(off).append(" figures are not at rest; ");
        return bad.toString();
    }

    /**
     * One clock speed of section 65 on the Bank tab (the Overview when `page` is null): the page is shown, a month lands (one that moved
     * the capital ratio, on the Overview) and it is shown again; the PROFIT figure pops and the capital gauge grows exactly where `expect`
     * says. Where nothing counts, nothing runs, every figure is at rest, and the page is the page built new. Null, or what was wrong.
     */
    static String bankRung(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect) throws Exception {
        bench.clock(!paused, null);
        bench.speed(at);
        bench.bankOpen(page == null ? null : "pages", page, false);
        bench.showBank();
        bench.finishPageAnimations();
        bench.passes();
        java.util.Set<Animation> before = animationsNow(bench);
        // The gauge grows only while the risk-weighted ratio binds, and no bank of this harness is held by it (its book is almost all equity, so the
        // leverage ratio binds, or the risk-weighted ratio is far above the bar's scale): the fixture causes the condition through the model's own
        // setter for the book weighed for risk, large enough that the risk-weighted ratio is the tighter and under the scale, for the month that
        // moved the ratio. (Bank.setWeightedBook is Game.refreshBank()'s: the next month's pass weighs the book again from the debts.)
        Bank bank = g.getBank();
        boolean moved = false;
        String saw = "";
        for (int i = 0; i < 12 && !moved; i++) {
            month(g);
            if (page != null) {
                moved = true;
            } else {
                steerGauge(g);
                moved = bench.bankGaugeMoved();
                double[] gauge = bench.gaugeNumbers();
                saw = "leverage binds: " + bank.leverageBinds() + "; the gauge says " + gauge[0] + ", the month before " + gauge[1] + ", on a scale of " + gauge[2];
            }
        }
        if (!moved) return "no month in twelve moved the capital ratio (" + saw + ")";
        return bankRungChecks(g, bench, page, at, paused, expect, before);
    }

    /** The Bank's risk-weighted ratio made the one that binds (see bankRung()): the book weighed for risk set to twenty times what the bank holds. */
    static void steerGauge(Game g) {
        Bank bank = g.getBank();
        bank.setWeightedBook(20 * (bank.exposure() + Math.abs(bank.equity())), 0, 0);
    }

    /** ...what the month that landed started, once shown (the weighted book still steered on the Overview, so that the page built new beside it is drawn on the same bank). */
    private static String bankRungChecks(Game g, KeptBench bench, String page, int at, boolean paused, boolean expect, java.util.Set<Animation> before) throws Exception {
        bench.showBank();
        bench.passes();
        int[] s = started(bench, before);
        StringBuilder bad = new StringBuilder();
        if ((s[0] > 0) != expect) bad.append(expect ? "the PROFIT figure did not pop; " : "the PROFIT figure popped; ");
        if (page == null && (s[1] > 0) != expect) bad.append(expect ? "the capital gauge did not grow; " : "the capital gauge grew; ");
        if (!expect) {
            bad.append(stillOrNot(bench));
            KeptBench.Page p = bench.bank("the page", page == null ? null : "pages", page, false);
            String d = againstNewAsIs(bench, p);
            if (d != null) bad.append("not the page built new: ").append(d.replace('\n', ' ')).append("; ");
        }
        return bad.length() == 0 ? null : "at " + (paused ? "a paused clock" : KeptBench.speedLadder()[at] + "x") + " on " + (page == null ? "the Overview" : page) + ": " + bad;
    }

    /** ...and the fund's Portfolio, through the tab's real show method: the worth counts on and a payment pops. */
    static String fundRung(Game g, KeptBench bench, int at, boolean paused, boolean expect) throws Exception {
        bench.clock(!paused, null);
        bench.speed(at);
        bench.financesOpen("The city's fund", "Portfolio");
        bench.showFinances();
        bench.finishPageAnimations();
        bench.passes();
        boolean ok = false;
        java.util.Set<Animation> before = null;
        int[] s = null;
        String still = "", saw = "";
        // (The hero writes the worth to two or three digits - "D$1.6B" - so it reads other words only every few months in a fund this size.)
        for (int i = 0; i < 48 && !ok; i++) {
            bench.finishPageAnimations();
            before = animationsNow(bench);
            String wasWorth = bench.fundWorth();
            month(g);
            bench.showFinances();
            bench.passes();
            // What the month started and left running is read first; then the count is run out, to see whether the worth read other words (a count starts
            // only on words that differ) and whether a holding paid: only a month that paid a holding and moved the worth is a month for this section.
            s = started(bench, before);
            still = stillOrNot(bench);
            boolean paid = bench.paidLines() > 0;
            bench.finishPageAnimations();
            bench.passes();
            ok = paid && !java.util.Objects.equals(wasWorth, bench.fundWorth());
            saw = "the last month: " + bench.paidLines() + " payments, the worth read \"" + wasWorth + "\" and then \"" + bench.fundWorth() + "\"";
        }
        if (!ok) return "at " + (paused ? "a paused clock" : KeptBench.speedLadder()[at] + "x") + ": no month in forty-eight paid a holding and moved the fund's worth (" + saw + ")";
        StringBuilder bad = new StringBuilder();
        if ((s[0] > 0) != expect) bad.append(expect ? "no payment popped; " : "a payment popped; ");
        if ((s[1] > 0) != expect) bad.append(expect ? "the worth did not count on; " : "the worth counted on; ");
        if (!expect) {
            bad.append(still);
            KeptBench.Page p = bench.finances("the page", "The city's fund", "Portfolio");
            String d = againstNewAsIs(bench, p);
            if (d != null) bad.append("not the page built new: ").append(d.replace('\n', ' ')).append("; ");
        }
        return bad.length() == 0 ? null : "at " + (paused ? "a paused clock" : KeptBench.speedLadder()[at] + "x") + " on the fund: " + bad;
    }

    /**
     * Section 65: the count-up only at 1x-10x, on the Bank (the PROFIT figure's pop on the Overview and on a page's status strip, the capital
     * gauge's growth) and the fund (the worth's count and a payment's pop). With the clock at each rung of the speed ladder and paused, a month
     * that lands starts them exactly at the rungs UserInterface.countsUp() allows (COUNT_UP_TOP); past it nothing animates, the page is the
     * month's, and an animation still running from the month before is stopped.
     */
    static void moneyCountUp(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 65: the count-up only at 1x-10x (Bank and the fund) ---");
        double[] ladder = KeptBench.speedLadder();
        double top = KeptBench.countUpTop();
        int countingRungs = 0;
        for (double speed : ladder) if (speed <= top) countingRungs++;
        System.out.printf("  the ladder has %d rungs, %d of them at or under COUNT_UP_TOP (%.0fx)%n", ladder.length, countingRungs, top);
        String[][] views = {{"bank", null, "the Bank Overview's PROFIT pop and capital gauge"}, {"bank", "Profit", "the Profit page's status strip"},
                {"fund", null, "the fund's worth and a payment"}};
        for (String[] view : views) {
            boolean bankView = view[0].equals("bank");
            StringBuilder wrong = new StringBuilder();
            fx(() -> {
                bench.forgetMoney();
                return null;
            });
            for (int rung = 0; rung <= ladder.length; rung++) {
                // The last pass is the clock stopped, at the top rung: a month stepped by hand counts at any speed.
                boolean paused = rung == ladder.length;
                int at = paused ? ladder.length - 1 : rung;
                boolean expect = paused || ladder[at] <= top;
                String result = fx(() -> bankView ? bankRung(g, bench, view[1], at, paused, expect) : fundRung(g, bench, at, paused, expect));
                if (result != null) wrong.append(wrong.length() == 0 ? "" : "; ").append(result);
            }
            if (wrong.length() > 0) System.out.println("  " + view[2] + ": " + wrong);
            check("a month landing starts " + view[2] + " exactly at the rungs countsUp() allows, and when paused (" + (ladder.length + 1) + " clocks)", wrong.length() == 0);

            // An animation running when the clock has been sped past the top: the next month sets the figures at once and stops it. On the Bank's
            // Overview the gauge's growth is among them, so the risk-weighted ratio is made the binding one as in bankRung().
            String stopped = fx(() -> {
                int slow = 0;
                while (ladder[slow] > top) slow++;
                boolean steer = bankView && view[1] == null;
                try {
                    bench.clock(true, null);
                    bench.speed(slow);
                    if (bankView) bench.bankOpen(view[1] == null ? null : "pages", view[1], false);
                    else bench.financesOpen("The city's fund", "Portfolio");
                    Runnable show = bankView ? bench::showBank : bench::showFinances;
                    show.run();
                    bench.finishPageAnimations();
                    bench.passes();
                    boolean ok = false;
                    for (int i = 0; i < 48 && !ok; i++) {
                        month(g);
                        if (steer) steerGauge(g);
                        show.run();
                        bench.passes();
                        boolean pops = running(bench, javafx.animation.ScaleTransition.class) > 0, counts = running(bench, javafx.animation.Timeline.class) > 0;
                        ok = pops && (steer || !bankView ? counts : true) && (bankView || bench.paidLines() > 0);
                    }
                    if (!ok) return "no animation was running to stop";
                    List<Animation> running = new ArrayList<>(bench.pageAnimations());
                    bench.speed(ladder.length - 1);
                    month(g);
                    show.run();
                    bench.passes();
                    // (An animation on a node the month took off the page - a payment's label for a holding that paid nothing now - runs out its 440 ms by itself.)
                    java.util.Set<Animation> onPage = animationsNow(bench);
                    for (Animation a : running) {
                        if (onPage.contains(a) && a.getStatus() != Animation.Status.STOPPED) return "an animation is still " + a.getStatus();
                    }
                    String bad = stillOrNot(bench);
                    if (!bad.isEmpty()) return bad;
                    KeptBench.Page p = bankView ? bench.bank("the page", view[1] == null ? null : "pages", view[1], false)
                            : bench.finances("the page", "The city's fund", "Portfolio");
                    String d = againstNewAsIs(bench, p);
                    return d == null ? null : "the page is not the page built new: " + d.replace('\n', ' ');
                } finally {
                    // (nothing to put back: the next month's pass weighs the book again from the debts)
                }
            });
            if (stopped != null) System.out.println("  " + view[2] + ": " + stopped);
            check("an animation running when a month lands past COUNT_UP_TOP is stopped on " + view[2] + ", and the page is the month's", stopped == null);
        }
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
    }

    /* ----- the boxes (66) ----- */

    /**
     * One box on a page, given the focus and typed into (a node's `setFocused` when this scene has no window to give the focus: the line says which): a
     * month landed through the real show method twice - the same node, its text, its caret and its focus; a chip pressed while the box has the focus - the
     * text a page built new shows in the box, the caret at its end, the focus still there; and the page then equal to one built new.
     */
    static void boxHeld(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show,
                        java.util.function.Supplier<javafx.scene.control.TextField> find, String typed, int caret, String chip) throws Exception {
        Object[] r = fx(() -> {
            bench.home(page);
            String refused = setup.on(bench);
            if (refused != null) return new Object[]{refused};
            show.run();
            bench.passes();
            bench.settle();
            javafx.scene.control.TextField box = find.get();
            if (box == null) return new Object[]{"the page has no such box"};
            String how = KeptBench.boxFocus(box, typed);
            box.positionCaret(caret);
            Object[] s0 = KeptBench.boxState(box);
            month(g);
            show.run();
            bench.passes();
            Object[] s1 = KeptBench.boxState(find.get());
            month(g);
            show.run();
            bench.passes();
            Object[] s2 = KeptBench.boxState(find.get());
            boolean pressed = bench.pressChip(chip);
            bench.passes();
            Object[] s3 = KeptBench.boxState(find.get());
            // What a page built new shows in the box, and whether the page is equal to it (the box let go first: a focus ring is not a page's).
            bench.showFresh(page);
            String freshText = find.get() == null ? null : find.get().getText();
            bench.restoreKeptPage();
            show.run();
            bench.passes();
            KeptBench.boxBlur(find.get());
            String d = againstNew(bench, page);
            return new Object[]{null, how, s0, s1, s2, pressed, s3, freshText, d};
        });
        if (r[0] != null) {
            System.out.println("  " + name + ": " + r[0]);
            check(name + ": the box can be reached (" + r[0] + ")", false);
            return;
        }
        String how = (String) r[1];
        Object[] s0 = (Object[]) r[2], s1 = (Object[]) r[3], s2 = (Object[]) r[4], s3 = (Object[]) r[6];
        System.out.printf("  %s took the focus by: %s; typed \"%s\" (caret %s), after a month \"%s\" (caret %s), a chip \"%s\" pressed: \"%s\" (caret %s), a page built new says \"%s\"%n",
                name, how, s0[1], s0[2], s1[1], s1[2], chip, s3[1], s3[2], r[7]);
        check(name + ": takes the focus (" + how + ")", how.equals("requestFocus") || how.equals("setFocused by reflection"));
        check(name + ": a month landing keeps the box (the same node)", s1[0] == s0[0] && s2[0] == s0[0]);
        check(name + ": ...its text (\"" + s2[1] + "\")", typed.equals(s1[1]) && typed.equals(s2[1]));
        check(name + ": ...its caret (" + s0[2] + " before, " + s1[2] + " and " + s2[2] + " after)", s0[2].equals(s1[2]) && s0[2].equals(s2[2]));
        check(name + ": ...and its focus", (boolean) s1[3] && (boolean) s2[3]);
        check(name + ": the chip \"" + chip + "\" is on the page, and pressed", (boolean) r[5]);
        check(name + ": a chip pressed while the box has the focus leaves the same node", s3[0] == s0[0]);
        check(name + ": ...sets its text to what a page built new shows (\"" + s3[1] + "\")", r[7] != null && r[7].equals(s3[1]));
        boolean moved = !s2[1].equals(s3[1]);
        check(name + (moved ? ": ...the caret at the end (" : ": ...the text unmoved by the chip, the caret where it was (") + s3[2] + ")",
                moved ? (int) s3[2] == ((String) s3[1]).length() : s3[2].equals(s2[2]));
        check(name + ": ...the focus still on it", (boolean) s3[3]);
        check(name + ": ...and the page is equal to a page built new", r[8] == null);
        if (r[8] != null) System.out.println("  " + r[8]);
    }

    /**
     * Section 66's Enter (KP2b, for KP5's mend in CapChips): a yield box given the focus, "4.5" typed and Enter fired on it. Enter sets the cap and draws the
     * page at once, and the box - the same node, kept - has to show what a page built new shows ("4.500", the cap's own yieldText), its caret at the end
     * and the focus still on it; a kept draw never sets a field under the player's typing, so it is CapChips that sets this one, because it differs from
     * what the page shows. `putBack` returns the cap Enter set to what the page opens with, before and after.
     */
    static void boxEnter(Game g, KeptBench bench, String name, KeptBench.Page page, Hand setup, Runnable show,
                         java.util.function.Supplier<javafx.scene.control.TextField> find, java.util.function.Supplier<Auction.Cap> capNow,
                         Runnable putBack, String typed) throws Exception {
        Object[] r = fx(() -> {
            putBack.run();
            bench.home(page);
            String refused = setup.on(bench);
            if (refused != null) return new Object[]{refused};
            show.run();
            bench.passes();
            bench.settle();
            javafx.scene.control.TextField box = find.get();
            if (box == null) return new Object[]{"the page has no such box"};
            String how = KeptBench.boxFocus(box, typed);
            Object[] s0 = KeptBench.boxState(box);
            // Enter: the box's own action, as the key fires it.
            box.fireEvent(new javafx.event.ActionEvent());
            bench.passes();
            Object[] s1 = KeptBench.boxState(find.get());
            Auction.Cap cap = capNow.get();
            // What a page built new shows in the box (the cap Enter set), and whether the page is equal to it (the box let go first).
            bench.showFresh(page);
            String freshText = find.get() == null ? null : find.get().getText();
            bench.restoreKeptPage();
            show.run();
            bench.passes();
            KeptBench.boxBlur(find.get());
            String d = againstNew(bench, page);
            putBack.run();
            return new Object[]{null, how, s0, s1, cap, freshText, d};
        });
        if (r[0] != null) {
            System.out.println("  " + name + ": " + r[0]);
            check(name + ": the box can be reached for Enter (" + r[0] + ")", false);
            return;
        }
        String how = (String) r[1];
        Object[] s0 = (Object[]) r[2], s1 = (Object[]) r[3];
        Auction.Cap cap = (Auction.Cap) r[4], named = KeptBench.yieldCapOf(typed);
        String shows = KeptBench.yieldText(cap);
        System.out.printf("  %s took the focus by: %s; typed \"%s\", Enter: \"%s\" (caret %s), the cap now %s, a page built new says \"%s\"%n",
                name, how, typed, s1[1], s1[2], shows, r[5]);
        check(name + ": takes the focus for Enter (" + how + ")", how.equals("requestFocus") || how.equals("setFocused by reflection"));
        check(name + ": Enter sets the cap to the yield typed (" + shows + ")", cap.mode() == Auction.Mode.YIELD && Math.abs(cap.value() - named.value()) < 1e-12);
        check(name + ": what was typed is not what the page shows for it (\"" + typed + "\", \"" + shows + "\"): the box has something to change", !typed.equals(shows));
        check(name + ": Enter leaves the same node", s1[0] == s0[0]);
        check(name + ": ...its text what the cap reads on a page built new (\"" + s1[1] + "\")", shows.equals(s1[1]) && shows.equals(r[5]));
        check(name + ": ...the caret at the end (" + s1[2] + ")", s1[1] != null && (int) s1[2] == ((String) s1[1]).length());
        check(name + ": ...the focus still on it", (boolean) s1[3]);
        check(name + ": ...and the page is equal to a page built new", r[6] == null);
        if (r[6] != null) System.out.println("  " + r[6]);
    }

    /** Section 66: the boxes keep the player's hand - Close it now's two, the ask's, the yield boxes of Borrow's and the rollover's chips (typed into, a chip pressed, and Enter), the fund's search box. */
    static void moneyControls(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 66: the boxes keep the player's hand ---");
        Debt piece = longestPiece(bench);
        List<String> moneys = KeptBench.borrowMoneys();
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 4, g.getCash() / 6));
        Auction.Cap rollover = g.getRolloverCap();
        Hand borrow = b -> {
            b.financesBorrow("Term", 10, moneys.get(0), ask, false, false);
            return null;
        };
        if (piece == null) {
            check("the city has a piece of paper to close", false);
        } else {
            KeptBench.Page close = fx(() -> bench.finances("Close it now", "The book", "Every piece"));
            boxHeld(g, bench, "Close it now's face box", close, closeOf(piece), bench::showFinances, () -> bench.financesCloseBoxes()[0], "1,000,000", 5, "half");
            boxHeld(g, bench, "Close it now's price box", close, closeOf(piece), bench::showFinances, () -> bench.financesCloseBoxes()[1], "98.5", 2, "no limit");
        }
        KeptBench.Page home = fx(() -> bench.finances("Borrow at home", "Borrow", "At home"));
        boxHeld(g, bench, "the ask's box", home, borrow, bench::showFinances, bench::financesAskBox, "2.5T", 2, "×10");
        boxHeld(g, bench, "Borrow's yield box", home, borrow, bench::showFinances, bench::yieldBox, "4.5", 2, "Fill it");
        KeptBench.Page hub = fx(bench::finances);
        boxHeld(g, bench, "the rollover's yield box", hub, b -> null, bench::showFinances, bench::yieldBox, "4.5", 2, "Fill it");
        boxEnter(g, bench, "Borrow's yield box", home, borrow, bench::showFinances, bench::yieldBox, bench::borrowCap, bench::financesCapDefault, "4.5");
        boxEnter(g, bench, "the rollover's yield box", hub, b -> null, bench::showFinances, bench::yieldBox, g::getRolloverCap, () -> g.setRolloverCap(rollover), "4.5");
        KeptBench.Page search = fx(() -> bench.finances("the fund's Search", "The city's fund", "Search"));
        boxHeld(g, bench, "the fund's search box", search, fundOn("Search"), bench::showFinances, bench::fundSearchBox, "auto", 2, "Shares");
        // What the chips set on the model and the screen is put back.
        fx(() -> {
            g.setRolloverCap(rollover);
            bench.financesCapDefault();
            bench.fundSearch("", "All");
            return null;
        });
    }

    /* ----- the hover (67) and the cost (68) ----- */

    /** Section 67: the hover. A Bank door card, a rung's name on the rate ladder, a Finances area card, a row of the book and a fund holding's row keep the look their own mouse-entered gives them across a kept draw, and the exit's once the pointer is off. */
    static void moneyHover(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 67: the hover is kept (Bank, Finances, the fund) ---");
        java.util.Map<String, Boolean> found = new java.util.LinkedHashMap<>();
        Debt piece = longestPiece(bench);
        List<Spot> spots = new ArrayList<>(List.of(
                new Spot("the Bank Overview", fx(bench::bank), b -> null),
                new Spot("the Bank's Lending page", fx(() -> bench.bank("the Bank's Lending page", "pages", "Lending", false)), b -> null),
                new Spot("the Finances hub", fx(bench::finances), b -> null),
                new Spot("the bond market", fx(() -> bench.finances("the bond market", "The bond market", "Every issue")), b -> null),
                new Spot("the fund's Portfolio", fx(() -> bench.finances("the fund's Portfolio", "The city's fund", "Portfolio")), b -> null)));
        String[][] kinds = {{"the Bank Overview", "bankCard"}, {"the Bank Overview", "rungName"}, {"the Bank's Lending page", "rungName"},
                {"the Finances hub", "financeCard"}, {"the Finances hub", "bookRow"}, {"the bond market", "financeCard"}, {"the fund's Portfolio", "holdingRow"}};
        for (String[] k : kinds) {
            Spot spot = null;
            for (Spot s : spots) if (s.label().equals(k[0])) spot = s;
            final Spot at = spot;
            String d = fx(() -> {
                bench.home(at.page());
                at.setup().on(bench);
                bench.drawKept(at.page());
                bench.passes();
                return bench.hoverKept(k[1], () -> bench.drawKept(at.page()));
            });
            hoverLine(found, at.label(), k[1], d);
        }
        for (String kind : List.of("bankCard", "rungName", "financeCard", "bookRow", "holdingRow")) {
            check("a " + kind + " was found on a money page of this city, to put the pointer on", found.getOrDefault(kind, false));
        }
    }

    /** The page of a tab that weighs most (the nodes its first draw makes), by name: printed with the others. */
    static KeptBench.Page heaviest(KeptBench bench, String tab, List<KeptBench.Page> pages) throws Exception {
        KeptBench.Page best = null;
        int most = -1;
        StringBuilder line = new StringBuilder();
        for (KeptBench.Page p : pages) {
            int nodes = fx(() -> {
                bench.home(p);
                bench.drawKept(p);
                bench.passes();
                return bench.counts(p)[0];
            });
            line.append(line.length() == 0 ? "" : "; ").append(p.name()).append(" ").append(String.format("%,d", nodes));
            if (nodes > most) {
                most = nodes;
                best = p;
            }
        }
        System.out.println("  " + tab + ", nodes a page makes: " + line + " -> the heaviest is " + best.name());
        return best;
    }

    /** Section 68: what the kept month costs on the heaviest page of the Bank, of Finances and of the fund (and the fund's Activity and Search). */
    static void moneyCost(Game g, KeptBench bench) throws Exception {
        KeptBench.Page bankOverview = fx(bench::bank);
        List<KeptBench.Page> bankPages = new ArrayList<>();
        bankPages.add(bankOverview);
        for (String p : KeptBench.bankPages()) bankPages.add(fx(() -> bench.bank("the Bank's " + p + " page", "pages", p, false)));
        bankPages.add(fx(() -> bench.bank("the Bank's Profit page as a statement", "pages", "Profit", true)));
        bankPages.add(fx(() -> bench.bank("the Bank's Balance sheet as a statement", "pages", "Balance sheet", true)));
        pageCost(g, bench, heaviest(bench, "the Bank", bankPages), 68, KEPT_BANK_MS, "KEPT_BANK_MS");

        KeptBench.Page financesHub = fx(bench::finances);
        List<KeptBench.Page> financePages = new ArrayList<>();
        financePages.add(financesHub);
        for (String area : KeptBench.financeAreas()) {
            if (area.equals("The city's fund")) continue;
            for (String p : KeptBench.financePages(area)) {
                if (p.equals("A bond's book")) continue;
                financePages.add(fx(() -> bench.finances(area + ", " + p, area, p)));
            }
        }
        pageCost(g, bench, heaviest(bench, "Finances", financePages), 68, KEPT_FINANCES_MS, "KEPT_FINANCES_MS");
        // ...and Borrow with an ask, which draws the auction and its picture besides.
        KeptBench.Page borrow = fx(() -> bench.finances("Borrow at home, asking for a sixth of the cash", "Borrow", "At home"));
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 4, g.getCash() / 6));
        fx(() -> {
            bench.financesBorrow("Term", 10, KeptBench.borrowMoneys().get(0), ask, false, false);
            return null;
        });
        pageCost(g, bench, borrow, 68, KEPT_FINANCES_MS, "KEPT_FINANCES_MS");

        List<KeptBench.Page> fundPages = new ArrayList<>();
        for (String p : KeptBench.financePages("The city's fund")) fundPages.add(fx(() -> bench.finances("the fund's " + p, "The city's fund", p)));
        fx(() -> {
            bench.fundOnShow(true);
            return null;
        });
        KeptBench.Page heavy = heaviest(bench, "the fund", fundPages);
        for (KeptBench.Page p : List.of(fundPages.get(1), fundPages.get(2))) {
            pageCost(g, bench, p, 68, KEPT_FUND_MS, "KEPT_FUND_MS", 5);
        }
        check("the fund's heaviest page is Search or Activity, the two costed (" + heavy.name() + ")", heavy == fundPages.get(1) || heavy == fundPages.get(2));
        fx(() -> {
            bench.fundOnShow(false);
            return null;
        });
    }

    /** Sections 55 to 68: the money screens kept. */
    static void money(Game g, KeptBench bench) throws Exception {
        long t = System.nanoTime();
        bank(g, bench);
        t = took("the Bank (55 to 57)", t);
        finances(g, bench);
        financesMenus(g, bench);
        t = took("Finances (58 to 60)", t);
        fund(g, bench);
        t = took("the fund (61 to 63)", t);
        moneyPictures(g, bench);
        t = took("the pictures (64)", t);
        moneyCountUp(g, bench);
        t = took("the count-up (65)", t);
        moneyControls(g, bench);
        moneyHover(g, bench);
        t = took("the boxes and the hover (66, 67)", t);
        moneyCost(g, bench);
        took("the cost (68)", t);
    }

    /** What a stretch of the money sections took, in a line; the clock's reading for the next. */
    private static long took(String what, long since) {
        long now = System.nanoTime();
        System.out.printf("  [%s took %.0f s]%n", what, (now - since) / 1e9);
        return now;
    }

    /* =====================================================================
       GOVERNMENT, TRADE & THE WORLD AND POLICY (KP6), sections 69 to 81: the three screens KP6 keeps, each page drawn kept from the
       screen's pool and built new from an empty one - kept, shown as a page built new shows it, kept right through what reshapes it -
       then the controls on Policy, the count-up the clock allows, the hover, and what it costs.
       ===================================================================== */

    /**
     * The budget for the kept Government month - its draw, then the pulse's CSS and layout - on the Overview, at month 3,500 of a played city (about
     * where KeptCheck is when it comes to the Government tab), on the PC: twice the kept month's median, rounded up (3.1 to 4.9 ms in five runs of the
     * KP6 sections alone, 3.4 between them; the same month built new 12.0 to 20.6 ms).
     */
    static final double KEPT_GOVERNMENT_MS = 7;

    /**
     * The budget for the kept Trade month - its draw, then the pulse's CSS and layout - on the Overview and on the heaviest of its pages (the reserves),
     * the larger of the two being the figure, at month 3,500 of a played city, on the PC: twice the kept month's median, rounded up (the Overview 4.3
     * to 9.0 ms in five runs of the KP6 sections alone, 4.7 between them, the same month built new 8.3 to 14.8 ms; the reserves page 2.7 to 4.3 ms,
     * 3.0 between them, built new 10.6 to 13.8).
     */
    static final double KEPT_TRADE_MS = 10;

    /**
     * The budget for the kept Policy month - its draw, then the pulse's CSS and layout - on the hub and on the pages heavy with dials (Taxes' Profit
     * page and the policy rate's), the largest of them being the figure, at month 3,500 of a played city, on the PC: twice the kept month's median,
     * rounded up (the hub 4.3 to 5.2 ms in five runs of the KP6 sections alone, 4.6 between them, built new 9.7 to 14.9 ms; Profit 2.0 to 3.3 ms, 2.1
     * between them, built new 6.9 to 8.9; the policy rate 4.2 to 7.2 ms, 4.5 between them, built new 15.9 to 26.5).
     */
    static final double KEPT_POLICY_MS = 10;

    /**
     * The share of a KP6 page's nodes a month's redraw has to hand back after a choice, at least: three in four. The pages of these screens rank what the
     * model did (the partners by what they traded, the payers by what they paid, the paper by what falls due), and a month that moves a row in or out of
     * a short table makes its nodes: at month 3,505 of a played city (441,000 people) one month made 80 of the 350 nodes of the Partners page - the
     * ranking of its eleven partners moved - and 44 of the 397 of Spending with every line open. A page that is remade by a redraw hands back next to
     * nothing, and sections 69, 72 and 75 hold the nine in ten (the average of 25 months, most of them making none).
     */
    static final double KP6_HANDED_BACK = .75;

    /** The assertions made so far, for the count a stretch of the KP6 sections reports. */
    static int checks = 0;

    /** What a stretch of the KP6 sections took and how many assertions it made, in a line; the clock's reading for the next. */
    private static long tookWith(String what, long since, int checksSince) {
        long now = System.nanoTime();
        System.out.printf("  [%s took %.0f s, %d assertions]%n", what, (now - since) / 1e9, checks - checksSince);
        return now;
    }

    /** The folds the Trade sections open that have to add to their page in any city that trades: the month's ledger and the eight moneys over the years. */
    static final java.util.Set<String> TRADE_FOLDS_THAT_ADD = java.util.Set.of("month:ledger", "reserves:eight");

    /**
     * The taxes cut to nothing, the central bank printing for the treasury and two lines of arrears owed; what the dials were. The fuel duty, the port
     * dues and the fund's withdrawal go too: in a city as large as KeptCheck's by now the fund's transfer is most of the treasury's income, and the
     * port dues more than it spends, so cutting the four taxes alone leaves a surplus.
     */
    static double[] makeDeficit(Game g) throws Exception {
        var tax = g.getEconomyManager().getTaxPolicy();
        double[] was = {tax.getProfitTaxRate(), tax.getSalesTaxRate(), tax.getWageTaxRate(), tax.getPropertyTaxRate(), tax.getFuelDuty(), tax.getPortDues(),
                g.getFund().getWithdrawal()};
        tax.setProfitTaxRate(0);
        tax.setSalesTaxRate(0);
        tax.setWageTaxRate(0);
        tax.setPropertyTaxRate(0);
        tax.setFuelDuty(0);
        tax.setPortDues(0);
        g.getFund().setWithdrawal(0);
        g.getCentralBank().advanceToTreasury(50_000);
        java.lang.reflect.Field f = Game.class.getDeclaredField("arrears");
        f.setAccessible(true);
        @SuppressWarnings("unchecked") java.util.Map<String, Double> arrears = (java.util.Map<String, Double>) f.get(g);
        java.lang.reflect.Method key = Game.class.getDeclaredMethod("arrearsKey", TreasuryLine.class, String.class);
        key.setAccessible(true);
        arrears.put((String) key.invoke(null, TreasuryLine.INTEREST, null), 1234.0);
        arrears.put((String) key.invoke(null, TreasuryLine.STUDENT_GRANTS, null), 567.0);
        return was;
    }

    /** The dials put back where makeDeficit() found them. */
    static void endDeficit(Game g, double[] was) {
        var tax = g.getEconomyManager().getTaxPolicy();
        tax.setProfitTaxRate(was[0]);
        tax.setSalesTaxRate(was[1]);
        tax.setWageTaxRate(was[2]);
        tax.setPropertyTaxRate(was[3]);
        tax.setFuelDuty(was[4]);
        tax.setPortDues(was[5]);
        g.getFund().setWithdrawal(was[6]);
    }

    /** How many lines of the page's signature hold a marker (a card the page draws only in some states). */
    static int linesSaying(KeptBench bench, String marker) {
        int n = 0;
        for (String l : bench.signature()) if (l.contains(marker)) n++;
        return n;
    }

    /**
     * The choices made to act (see Choice.acts): for a page whose content is the model's ranking or the sign of its budget, where a month that moves
     * a row in or out, or turns a surplus into a deficit, makes the nodes of the cards it swaps - a share of the page that says nothing of what is
     * kept. Each redraw is still compared with a page built new; the share handed back is printed and not asserted.
     */
    static List<Choice> asActing(List<Choice> choices) {
        List<Choice> out = new ArrayList<>();
        for (Choice c : choices) out.add(new Choice(c.name(), c.hand(), c.opens(), true));
        return out;
    }

    /** The Government tab's pages, folds and lines as choices (the folds that must add to their page only where the city has what they fold). */
    static List<Choice> governmentChoices(boolean debt) {
        List<Choice> c = new ArrayList<>();
        for (String p : KeptBench.governmentPages()) {
            c.add(choice(p + " page", b -> { b.governmentOpen(p); return null; }));
            for (String f : KeptBench.governmentFolds()) {
                boolean here = (f.equals("output") && p.equals("Output")) || (f.equals("treasury") && p.equals("Overview"))
                        || (debt && f.equals("debt") && p.equals("Spending"));
                c.add(new Choice(p + ": fold " + f + " opened", b -> { b.governmentFold(f, true); return null; }, here));
                c.add(choice(p + ": fold " + f + " closed", b -> { b.governmentFold(f, false); return null; }));
            }
            for (String line : KeptBench.governmentLines(p)) {
                c.add(choice(p + ": " + line + " opened", b -> { b.governmentLine(p, line, true); return null; }));
                c.add(choice(p + ": " + line + " closed", b -> { b.governmentLine(p, line, false); return null; }));
            }
            // Everything at once, and then everything closed.
            c.add(choice(p + ": every fold and line open", b -> {
                for (String f : KeptBench.governmentFolds()) b.governmentFold(f, true);
                for (String line : KeptBench.governmentLines(p)) b.governmentLine(p, line, true);
                return null;
            }));
            c.add(choice(p + ": every fold and line closed", b -> {
                for (String f : KeptBench.governmentFolds()) b.governmentFold(f, false);
                for (String line : KeptBench.governmentLines(p)) b.governmentLine(p, line, false);
                return null;
            }));
        }
        return c;
    }

    /** How many of a Government page's lines add to it when opened into who pays: {added, tried}. */
    static int[] linesThatAdd(KeptBench bench, KeptBench.Page tab, String page) throws Exception {
        return fx(() -> {
            bench.home(tab);
            bench.governmentOpen(page);
            bench.drawKept(tab);
            bench.passes();
            bench.settle();
            int base = bench.signature().size(), added = 0, tried = 0;
            for (String line : KeptBench.governmentLines(page)) {
                bench.governmentLine(page, line, true);
                bench.drawKept(tab);
                bench.passes();
                bench.settle();
                tried++;
                if (bench.signature().size() > base) added++;
                bench.governmentLine(page, line, false);
            }
            bench.drawKept(tab);
            return new int[]{added, tried};
        });
    }

    /** Sections 69 to 71: the Government tab kept. */
    static void kp6Government(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The Government tab (KP6) ===");
        // The clock paused: a month stepped by hand counts up at any speed (the Government tab has no animation to run, but the other two do).
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        KeptBench.Page overview = fx(bench::government);
        List<KeptBench.Page> pages = new ArrayList<>(List.of(overview));
        for (String p : KeptBench.governmentPages()) {
            if (!p.equals("Overview")) pages.add(fx(() -> bench.government("the Government " + p + " page", p)));
        }
        // (The Overview's two lists show a line in the months it took in or paid out something, so a line that comes back makes its row: in a city like
        // KeptCheck's by now it is most months, 17 nodes of 320 at the most. No month may make more than a tenth of the page; the quiet months are printed.)
        for (KeptBench.Page p : pages) pageKept(g, bench, p, 69, p == overview);
        for (KeptBench.Page p : pages) {
            arrive(bench, p);
            pageShows(g, bench, p, 70);
        }

        // 71: what reshapes the tab. The pages in turn, the folds and the lines inside them, and a door's target.
        KeptBench.Page tab = fx(bench::government);
        List<Choice> pick = new ArrayList<>();
        for (String p : KeptBench.governmentPages()) pick.add(choice(p, b -> { b.governmentOpen(p); return null; }));
        pick.add(choice("the Overview again", b -> { b.governmentOpen("Overview"); return null; }));
        pageReshaped(g, bench, tab, 71, pick, KP6_HANDED_BACK);
        for (String page : List.of("Revenue", "Spending")) {
            int[] r = linesThatAdd(bench, tab, page);
            check(page + ": the lines that open into who pays add to the page when opened (" + r[0] + " of " + r[1] + ")", r[0] >= 1);
        }
        pageReshaped(g, bench, tab, 71, governmentChoices(false), KP6_HANDED_BACK);
        List<Choice> doors = new ArrayList<>();
        doors.add(choice("shown through showGovernmentMenu()", b -> { b.governmentOpen("Overview"); b.forgetGovernment(); b.showGovernment(); return null; }));
        for (String p : KeptBench.governmentPages()) doors.add(choice("the chip " + p + " picked", b -> { b.governmentPick(p); return null; }));
        doors.add(choice("a door to the bridge", b -> { b.governmentDoor("Overview", "#bridge"); return null; }));
        doors.add(choice("a door to the balance", b -> { b.governmentDoor("Overview", "#balance"); return null; }));
        doors.add(choice("a door to Revenue's Business tax", b -> { b.governmentDoor("Revenue", "Business tax"); return null; }));
        doors.add(choice("a door to Spending's Pensions", b -> { b.governmentDoor("Spending", "Pensions"); return null; }));
        doors.add(choice("a door to Spending's interest line (the paper)", b -> { b.governmentDoor("Spending", "Interest on the city's paper"); return null; }));
        doors.add(choice("a door to the Overview", b -> { b.governmentDoor("Overview", null); return null; }));
        pageReshaped(g, bench, tab, 71, doors, KP6_HANDED_BACK);

        // The city owing: one piece of every kind of paper, then the months the notes fall due. The debt fold and the paper's lines have to add to the page.
        int owed = fx(() -> bench.financesPieces().size());
        if (owed < PAPER.length) {
            System.out.println("  the city owes " + owed + " pieces of paper; selling one of every kind");
            sellPaper(g, bench);
        }
        System.out.println("  the city owes " + fx(() -> bench.financesPieces().size()) + " pieces of paper");
        pageReshaped(g, bench, tab, 71, governmentChoices(true), KP6_HANDED_BACK);
        List<Choice> years = new ArrayList<>();
        years.add(choice("Spending, the interest line and the debt fold open", b -> {
            b.governmentOpen("Spending");
            b.governmentLine("Spending", "Interest on the city's paper", true);
            b.governmentFold("debt", true);
            return null;
        }));
        for (int i = 1; i <= 14; i++) years.add(choice("a month on (" + i + ")", b -> null));
        years.add(choice("the Overview, the bridge and the treasury open", b -> {
            b.governmentOpen("Overview");
            b.governmentFold("bridge:0", true);
            b.governmentFold("bridge:1", true);
            b.governmentFold("treasury", true);
            return null;
        }));
        for (int i = 1; i <= 3; i++) years.add(choice("a month on, the Overview (" + i + ")", b -> null));
        pageReshaped(g, bench, tab, 71, years, KP6_HANDED_BACK);

        // A deficit, the central bank printing for the treasury and arrears owed: the cards the Overview draws only then.
        double[] rates = fx(() -> makeDeficit(g));
        for (int m = 0; m < 3; m++) fx(() -> { month(g); return null; });
        int[] marks = fx(() -> {
            bench.home(tab);
            bench.drawKept(tab);
            bench.passes();
            return new int[]{linesSaying(bench, "CENTRAL BANK"), linesSaying(bench, "OWED AND UNPAID"), linesSaying(bench, "DEFICIT")};
        });
        System.out.printf("  the Overview in a deficit: CENTRAL BANK lines %d, OWED AND UNPAID lines %d, DEFICIT lines %d, arrears owed: %s%n",
                marks[0], marks[1], marks[2], fx(g::hasArrears));
        check("the central bank's printing and the arrears owed cause the cards they are meant to (CENTRAL BANK lines " + marks[0] + ", OWED AND UNPAID lines " + marks[1] + ")",
                marks[0] > 0 && marks[1] > 0);
        // (The sign of the budget turns where the treasury's income stops: in the city KeptCheck has played to by now the central bank's remittance and the
        // fund's marks leave a surplus all the same, and the DEFICIT card is not reached by this fixture there; it is, in a city of 600 months.)
        System.out.println("  the budget in this deficit: " + (marks[2] > 0 ? "a deficit (DEFICIT lines " + marks[2] + ")" : "still a surplus: the DEFICIT card is not reached by a fixture in this city"));
        // (In a deficit made by hand the budget's sign turns from month to month with a lump of land sold or of paper repaid, and the cards swap.)
        pageReshaped(g, bench, tab, 71, asActing(governmentChoices(true)), KP6_HANDED_BACK);
        // ...and the taxes back, a few months on: the cards go, the surplus returns.
        fx(() -> {
            endDeficit(g, rates);
            return null;
        });
        List<Choice> back = new ArrayList<>();
        for (int i = 0; i < 6; i++) back.add(acting("a month with the taxes back, the Overview " + (i + 1), b -> { b.governmentOpen("Overview"); return null; }));
        for (String p : KeptBench.governmentPages()) back.add(choice(p, b -> { b.governmentOpen(p); return null; }));
        pageReshaped(g, bench, tab, 71, back, KP6_HANDED_BACK);
        fx(() -> {
            bench.home(tab);
            return null;
        });
    }

    /* ----- Trade ----- */

    /** The hand that puts the Trade tab on a page. */
    static Hand tradeOn(String page) {
        return b -> {
            b.tradeOpen(page);
            return null;
        };
    }

    /** The hand that puts the Trade tab on a nation's page behind Partners (0 is the table). */
    static Hand tradeNationOn(int nation) {
        return b -> {
            b.tradeNationPick(nation);
            return null;
        };
    }

    /** A fold or a line of the Trade tab opened or closed. */
    static Hand tradeFold(String key, boolean open) {
        return b -> {
            b.tradeLine(key, open);
            return null;
        };
    }

    /** Sections 72 to 74: Trade & the World kept. */
    static void kp6Trade(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The Trade tab (KP6) ===");
        // The clock paused: a month stepped by hand counts up at any speed, so the pages are drawn with the strip's figures counting and the bars
        // growing (and finished where a signature is read), the harder case for a page kept.
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        List<String> names = KeptBench.tradePages();
        check("the tab has its six pages (" + names.size() + ")", names.size() == 6);
        final String overviewName = names.get(0), monthName = names.get(1), goodsName = names.get(2), partnersName = names.get(3),
                currencyName = names.get(4), reservesName = names.get(5);
        List<Integer> partners = fx(() -> bench.tradePartners(3));
        List<Good> goods = fx(bench::tradeGoods);
        ForeignAccounts.Mix mix = g.getForeignAccounts().getReserveMix();
        System.out.println("  the city trades with nations " + partners + " and " + goods.size() + " goods are on the Partners board");
        check("the city trades with three nations, whose pages can be opened (" + partners.size() + ")", partners.size() >= 3);

        KeptBench.Page overview = fx(bench::trade);
        List<KeptBench.Page> pages = new ArrayList<>(List.of(overview));
        for (String p : names) if (!p.equals(overviewName)) pages.add(fx(() -> bench.trade("the Trade tab's " + p + " page", p)));
        KeptBench.Page nationPage = partners.isEmpty() ? null : fx(() -> bench.tradeNation("a nation's page behind Partners", partners.get(0)));
        if (nationPage != null) pages.add(nationPage);
        for (KeptBench.Page p : pages) pageKept(g, bench, p, 72);
        for (KeptBench.Page p : pages) {
            arrive(bench, p);
            pageShows(g, bench, p, 73);
        }

        // 74: what reshapes the tab.
        KeptBench.Page tab = fx(bench::trade);
        List<Choice> pick = new ArrayList<>();
        for (String p : names) pick.add(choice(p, tradeOn(p)));
        pick.add(choice(overviewName + " again", tradeOn(overviewName)));
        pageReshaped(g, bench, tab, 74, pick, KP6_HANDED_BACK);

        // Every page's folds and lines, opened outermost first and closed innermost first; the ones that must add to their page say so.
        List<Choice> folds = new ArrayList<>();
        for (String p : names) {
            // (The Partners table ranks the nations by what they traded: a month that moves one into or out of it makes its row, and with a few
            // partners left that is much of the page; its share is printed, and not asserted.)
            boolean ranked = p.equals(partnersName);
            folds.add(new Choice(p + " page", tradeOn(p), false, ranked));
            List<String> keys = KeptBench.tradeFolds(p);
            for (String key : keys) folds.add(new Choice(p + ": the fold " + key + " opened", tradeFold(key, true), TRADE_FOLDS_THAT_ADD.contains(key), ranked));
            List<String> back = new ArrayList<>(keys);
            Collections.reverse(back);
            for (String key : back) folds.add(new Choice(p + ": the fold " + key + " closed", tradeFold(key, false), false, ranked));
        }
        pageReshaped(g, bench, tab, 74, folds, KP6_HANDED_BACK);

        // The Month page on the river and on the steps; What we trade by business and by good.
        List<Choice> toggles = new ArrayList<>();
        toggles.add(choice(monthName + " page", tradeOn(monthName)));
        toggles.add(choice("the river", b -> { b.tradeRiver(true); return null; }));
        toggles.add(new Choice("the river, the ledger opened", b -> { b.tradeLine("month:ledger", true); return null; }, true));
        toggles.add(choice("the steps again", b -> { b.tradeRiver(false); return null; }));
        toggles.add(choice("the ledger closed", tradeFold("month:ledger", false)));
        toggles.add(choice(goodsName + " page", tradeOn(goodsName)));
        toggles.add(choice("by business", b -> { b.tradeByBusiness(true); return null; }));
        toggles.add(choice("by business, every line", tradeFold("goods:all", true)));
        toggles.add(choice("by good, every line", b -> { b.tradeByBusiness(false); return null; }));
        toggles.add(choice("by good, the first lines", tradeFold("goods:all", false)));
        pageReshaped(g, bench, tab, 74, toggles, KP6_HANDED_BACK);

        // Partners: the span, the goods, the nations' pages and back to the table.
        List<Choice> table = new ArrayList<>();
        table.add(choice(partnersName + " page", tradeOn(partnersName)));
        table.add(choice("the table of the year", b -> { b.tradeSpan(true); return null; }));
        table.add(choice("the table of the month", b -> { b.tradeSpan(false); return null; }));
        for (int i = 0; i < Math.min(4, goods.size()); i++) {
            final Good good = goods.get(i);
            table.add(choice("the board of " + good, b -> { b.tradeGood(good); return null; }));
        }
        table.add(choice("the board, every good open", tradeFold("partners:goods", true)));
        if (goods.size() > 6) {
            final Good far = goods.get(goods.size() - 1);
            table.add(choice("the board of " + far, b -> { b.tradeGood(far); return null; }));
        }
        table.add(choice("the board, every good closed", tradeFold("partners:goods", false)));
        for (int nation : partners) {
            table.add(choice("nation " + nation + "'s page", tradeNationOn(nation)));
            table.add(choice("nation " + nation + ", the rest of its goods", tradeFold(KeptBench.tradeNationFold(nation), true)));
            table.add(choice("nation " + nation + ", that fold closed", tradeFold(KeptBench.tradeNationFold(nation), false)));
            table.add(choice("back to the table", tradeNationOn(0)));
        }
        if (partners.size() >= 2) {
            table.add(choice("nation " + partners.get(0) + "'s page", tradeNationOn(partners.get(0))));
            table.add(choice("nation " + partners.get(1) + "'s page straight after", tradeNationOn(partners.get(1))));
            table.add(choice("back to the table", tradeNationOn(0)));
        }
        // (The Partners table ranks the nations by what they traded: a month that moves one into or out of it makes its row, and with a few partners left that is much of the page.)
        pageReshaped(g, bench, tab, 74, asActing(table), KP6_HANDED_BACK);

        // The reserves: the exchange's ask, buying and selling, the eight moneys and the mix of the vault.
        List<Choice> vault = new ArrayList<>();
        vault.add(choice(reservesName + " page", tradeOn(reservesName)));
        vault.add(choice("an ask of 1,000", b -> { b.tradeAsk(1000); return null; }));
        vault.add(choice("an ask of 100,000", b -> { b.tradeAsk(100_000); return null; }));
        vault.add(choice("selling", b -> { b.tradeBuying(false); return null; }));
        vault.add(choice("selling, an ask of 1,000", b -> { b.tradeAsk(1000); return null; }));
        vault.add(choice("buying again, nothing asked", b -> { b.tradeBuying(true); b.tradeAsk(0); return null; }));
        vault.add(choice("the chosen mix", b -> { g.getForeignAccounts().setReserveMix(ForeignAccounts.Mix.CHOSEN); return null; }));
        vault.add(choice("the world's mix", b -> { g.getForeignAccounts().setReserveMix(ForeignAccounts.Mix.WORLD_MIX); return null; }));
        for (String code : WorldMoneys.EIGHT) {
            vault.add(choice("buying the " + code + ", an ask of 5,000", b -> { b.tradeBuying(true); b.tradeMoney(code); b.tradeAsk(5000); return null; }));
            vault.add(choice("selling the " + code + ", an ask of 5,000", b -> { b.tradeBuying(false); b.tradeMoney(code); b.tradeAsk(5000); return null; }));
        }
        vault.add(choice("the mix put back, buying with nothing asked", b -> {
            b.tradeBuying(true);
            b.tradeMoney(null);
            b.tradeAsk(0);
            g.getForeignAccounts().setReserveMix(mix);
            return null;
        }));
        vault.add(new Choice("the eight over the years opened", tradeFold("reserves:eight", true), true));
        vault.add(choice("the eight over the years closed", tradeFold("reserves:eight", false)));
        pageReshaped(g, bench, tab, 74, vault, KP6_HANDED_BACK);

        // The fold "the eight over the years" (VaultTable): its two charts take the card's width on a kept draw as they do on a page built new.
        Object[] charts = fx(() -> {
            bench.home(tab);
            bench.tradeOpen(reservesName);
            bench.tradeLine("reserves:eight", true);
            bench.drawKept(tab);
            bench.passes();
            bench.settle();
            List<double[]> first = bench.chartWidths();
            month(g);
            bench.drawKept(tab);
            bench.passes();
            bench.settle();
            List<double[]> later = bench.chartWidths();
            String d = againstNew(bench, tab);
            bench.home(tab);
            return new Object[]{first, later, d};
        });
        @SuppressWarnings("unchecked") List<double[]> first = (List<double[]>) charts[0], later = (List<double[]>) charts[1];
        System.out.println("  the eight over the years: " + first.size() + " charts, " + String.format("%.0f", first.isEmpty() ? 0 : first.get(0)[0]) + " px wide in a box "
                + String.format("%.0f", first.isEmpty() ? 0 : first.get(0)[1]) + " px wide");
        check("the fold of the eight over the years holds its charts (" + first.size() + ", a month later " + later.size() + ")", !first.isEmpty() && first.size() == later.size());
        boolean fits = !first.isEmpty(), moved = !first.isEmpty();
        // (A chart is drawn 16 px less than its box and takes the box's width with its padding; made before it has a box it is 794 + 16 wide.)
        for (double[] c : first) {
            fits &= Math.abs(c[0] - c[1]) < 1.5;
            moved &= Math.abs(c[1] - (794 + 16)) > 1;
        }
        for (double[] c : later) fits &= Math.abs(c[0] - c[1]) < 1.5;
        check("...each chart as wide as the card it sits in, on the kept draw and the month after", fits);
        check("...and the card is not as wide as a chart made before it has a card (794 px and its padding): the width is the card's", moved);
        check("...the page equal to a page built new", charts[2] == null);
        if (charts[2] != null) System.out.println("  " + charts[2]);

        // A door's target, through the real show method.
        List<Choice> doors = new ArrayList<>();
        doors.add(choice(currencyName + " page", tradeOn(currencyName)));
        doors.add(choice("a door to the debt card", b -> { b.tradeDoor(currencyName, "#debt"); return null; }));
        doors.add(choice("a door to the forces card", b -> { b.tradeDoor(currencyName, "#forces"); return null; }));
        doors.add(choice("a door to the leave card", b -> { b.tradeDoor(reservesName, "#leave"); return null; }));
        doors.add(choice("a door to the cover card", b -> { b.tradeDoor(reservesName, "#cover"); return null; }));
        doors.add(choice("a door to the eight", b -> { b.tradeDoor(reservesName, "#eight"); return null; }));
        doors.add(choice("a door to the exchange, selling", b -> { b.tradeBuying(false); b.tradeDoor(reservesName, "#exchange"); return null; }));
        doors.add(choice("a door to the holdings", b -> { b.tradeBuying(true); b.tradeDoor(monthName, "#holdings"); return null; }));
        doors.add(choice("shown through showForeignMenu(), forgotten, and shown", b -> { b.showTrade(); b.forgetTrade(); b.showTrade(); return null; }));
        pageReshaped(g, bench, tab, 74, doors, KP6_HANDED_BACK);
        fx(() -> {
            g.getForeignAccounts().setReserveMix(mix);
            bench.home(tab);
            return null;
        });
    }

    /* ----- Policy ----- */

    /** The hand that puts Policy on an area and a page (the hub when `area` is null). */
    static Hand policyOn(String area, String page) {
        return b -> {
            b.policyOpen(area, page);
            return null;
        };
    }

    /** A choice that opens a page of Policy. */
    static Choice policyPage(String area, String page) {
        return choice(area + " › " + page, policyOn(area, page));
    }

    /** A choice that stages a change under a lever's key. */
    static Choice stage(String name, String key, double value) {
        return choice(name, b -> { b.policyStage(key, value); return null; });
    }

    /** A choice that drops everything staged. */
    static Choice drop(String name) {
        return choice(name, b -> { b.policyDrop(); return null; });
    }

    /** A fold or a line of Policy, opened or closed, as a choice (`opens` for a fold that has to add to its page). */
    static Choice policyFold(String name, String key, boolean open, boolean adds) {
        return new Choice(name, b -> { b.policyLine(key, open); return null; }, adds);
    }

    /** Every fold and line of Policy closed. */
    static Choice policyAllClosed(String name) {
        return choice(name, b -> {
            for (String k : b.policyLines()) b.policyLine(k, false);
            return null;
        });
    }

    /** Sections 75 to 77: Policy and zoning kept. */
    static void kp6Policy(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The Policy tab (KP6) ===");
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        boolean zoningOpen = fx(() -> g.isLeverOpen(PolicyDates.Lever.ZONING));
        // Zones made through the model, so that the ground's page has zones to list and each zone's page is a zoned sector's.
        boolean zoned = zoningOpen && fx(() -> bench.policyZone(Sectors.AGRICULTURE, .05) & bench.policyZone(Sectors.RETAIL, .02));
        System.out.println("  zoning is " + (zoningOpen ? "open" : "shut") + "; zones made through the model: " + zoned + " (" + fx(bench::policyZoned) + ")");

        KeptBench.Page hub = fx(bench::policy);
        final String ground = "The ground";
        final String agri = KeptBench.policyZonePage(Sectors.AGRICULTURE);
        List<KeptBench.Page> pages = new ArrayList<>(List.of(hub));
        pages.add(fx(() -> bench.policy("Policy, Taxes' Profit page", "Taxes", "Profit")));
        pages.add(fx(() -> bench.policy("Policy, Money's policy rate page", "Money", "The policy rate")));
        pages.add(fx(() -> bench.policy("Policy, Promises' Schools page", "Promises", "Schools")));
        pages.add(fx(() -> bench.policy("Policy, Promises' Food page", "Promises", "Food")));
        pages.add(fx(() -> bench.policy("Policy, Zoning's ground", "Zoning", ground)));
        if (zoned) pages.add(fx(() -> bench.policy("Policy, a zone's page", "Zoning", agri)));
        for (KeptBench.Page p : pages) pageKept(g, bench, p, 75);
        for (KeptBench.Page p : pages) {
            arrive(bench, p);
            pageShows(g, bench, p, 76);
        }

        // 77: what reshapes the tab. Every fixed page of the five areas, and the hub.
        List<Choice> every = new ArrayList<>();
        every.add(choice("the hub", policyOn(null, null)));
        for (String[] p : KeptBench.policyPages()) every.add(policyPage(p[0], p[1]));
        every.add(choice("the hub again", policyOn(null, null)));
        pageReshaped(g, bench, hub, 77, every, KP6_HANDED_BACK);

        // A zone's page for every sector, zoned or not; the ground with zones, its picker; a zone staged and dropped; the forest law.
        List<Choice> zones = new ArrayList<>();
        for (String s : KeptBench.policySectors()) zones.add(policyPage("Zoning", KeptBench.policyZonePage(s)));
        zones.add(policyPage("Zoning", ground));
        zones.add(policyFold("the ground, the picker open", "zoning:pick", true, false));
        zones.add(policyFold("the ground, the picker closed", "zoning:pick", false, false));
        if (zoned) {
            zones.add(policyPage("Zoning", agri));
            zones.add(stage("a zone's size staged", "zone:" + Sectors.AGRICULTURE + ":size", .08));
            zones.add(stage("...its price staged", "zone:" + Sectors.AGRICULTURE + ":price", .5));
            zones.add(stage("...its unit in km2", "zone:" + Sectors.AGRICULTURE + ":unit", 1));
            zones.add(stage("...a profit offset staged", "profit:" + Sectors.AGRICULTURE, .01));
            zones.add(stage("...the zone taken off (staged)", "zone:" + Sectors.AGRICULTURE + ":off", 1));
            zones.add(drop("...everything dropped"));
        }
        zones.add(policyPage("Zoning", "The forest law"));
        zones.add(stage("the forest law's protected share staged", "forest:protected", .2));
        zones.add(stage("...the kept share staged", "forest:kept", .4));
        zones.add(drop("...dropped"));
        pageReshaped(g, bench, hub, 77, zones, KP6_HANDED_BACK);

        // Folds and lines: each tax's table, a payer's own move, the zero payers, the floor's table, the rate's history, a course's own price, the food rows.
        List<Choice> folds = new ArrayList<>();
        for (String which : List.of("Profit", "Sales", "Wage", "Property")) {
            folds.add(policyPage("Taxes", which));
            folds.add(policyFold(which + ": the table fold opened", "table:" + which, true, true));
            String first = which.equals("Wage") ? "payer:wage:" + WageBand.values()[0].name()
                    : which.equals("Property") ? "payer:prop:" + Sectors.KEYS[0] : "payer:" + which.toLowerCase() + ":" + Sectors.KEYS[0];
            folds.add(policyFold(which + ": a payer's own move opened", first, true, false));
            folds.add(policyFold(which + ": the zero payers folded open", "zero:" + which, true, false));
            folds.add(choice(which + ": every payer's move opened", b -> {
                for (String k : Sectors.KEYS) {
                    b.policyLine("payer:profit:" + k, true);
                    b.policyLine("payer:sales:" + k, true);
                    b.policyLine("payer:prop:" + k, true);
                }
                for (WageBand band : WageBand.values()) b.policyLine("payer:wage:" + band.name(), true);
                return null;
            }));
            folds.add(policyAllClosed(which + ": all closed"));
        }
        folds.add(policyPage("Wages", "The floor"));
        folds.add(policyFold("the floor's table opened", "floor:table", true, true));
        folds.add(policyFold("the floor's table closed", "floor:table", false, false));
        folds.add(policyPage("Money", "The policy rate"));
        folds.add(policyFold("the rate page's history fold opened", "rate:history", true, true));
        folds.add(policyFold("...closed", "rate:history", false, false));
        folds.add(policyPage("Promises", "Schools"));
        for (EducationType kind : EducationType.values()) {
            if (kind != EducationType.NONE) folds.add(policyFold("a course's own price opened: " + kind.name(), "course:" + kind.name(), true, false));
        }
        folds.add(policyAllClosed("every course closed"));
        folds.add(policyPage("Promises", "Food"));
        folds.add(choice("the food page's rows opened", b -> {
            for (int r = 0; r < 12; r++) b.policyLine("aid:" + r, true);
            return null;
        }));
        folds.add(policyAllClosed("...closed"));
        pageReshaped(g, bench, hub, 77, folds, KP6_HANDED_BACK);

        // A change staged on a page: the tray, and the dial cards drawn at the thumb.
        List<Choice> staged = new ArrayList<>();
        staged.add(policyPage("Taxes", "Everything"));
        staged.add(stage("every tax at once staged", "income", .21));
        staged.add(stage("...the property rate staged", "property", .012));
        staged.add(drop("...dropped"));
        for (String which : List.of("Profit", "Sales", "Wage", "Property")) {
            staged.add(policyPage("Taxes", which));
            String key = which.equals("Property") ? "property" : "base:" + which.toLowerCase();
            staged.add(stage(which + ": the base rate staged", key, which.equals("Property") ? .013 : .27));
            staged.add(stage(which + ": a sector's offset staged too", (which.equals("Property") ? "prop:" : which.equals("Wage") ? "wage:" : which.toLowerCase() + ":")
                    + (which.equals("Wage") ? WageBand.values()[0].name() : Sectors.KEYS[0]), .01));
            staged.add(drop(which + ": dropped"));
        }
        staged.add(policyPage("Taxes", "Fuel"));
        staged.add(stage("the fuel duty staged", "fuelDuty", .00003));
        staged.add(drop("...dropped"));
        staged.add(policyPage("Taxes", "Port dues"));
        staged.add(stage("the port dues staged", "portDues", 2.0));
        staged.add(drop("...dropped"));
        staged.add(policyPage("Wages", "The floor"));
        staged.add(stage("the floor staged", "floor", 3000));
        staged.add(drop("...dropped"));
        staged.add(policyPage("Money", "The policy rate"));
        staged.add(stage("the policy rate staged", "policy", .07));
        staged.add(drop("...dropped"));
        for (String[] pk : new String[][] {{"Pensions", "contrib", "pension"}, {"Out of work", "eiPremium", "eiBenefit"}, {"Food", "foodAssistance", "foodAssistance"},
                {"Health", "healthFee", "healthPremium"}}) {
            staged.add(policyPage("Promises", pk[0]));
            staged.add(stage(pk[0] + ": " + pk[1] + " staged", pk[1], pk[1].equals("healthFee") ? 2.0 : .03));
            staged.add(stage(pk[0] + ": " + pk[2] + " staged too", pk[2], pk[2].equals("pension") ? .5 : .03));
            staged.add(drop(pk[0] + ": dropped"));
        }
        staged.add(policyPage("Promises", "Schools"));
        staged.add(stage("the schools' share staged", "tuition", .6));
        staged.add(stage("...every school's price staged", "tuitionScale", 1.5));
        staged.add(choice("...a course's own price staged", b -> { b.policyLine("course:UNIVERSITY", true); b.policyStage("tuitionScale:UNIVERSITY", 2.0); return null; }));
        staged.add(stage("...the loan rate staged", "loanRate", .03));
        staged.add(choice("...the grant's basis staged", b -> { b.policyStage("grantBasis", 1); b.policyStage("grantAmount", .2); return null; }));
        staged.add(choice("...dropped", b -> { b.policyDrop(); b.policyLine("course:UNIVERSITY", false); return null; }));
        pageReshaped(g, bench, hub, 77, staged, KP6_HANDED_BACK);

        // The currency page and the reform page under every regime the city can reach (the regime put back after).
        ForeignAccounts.Regime regimeWas = g.getForeignAccounts().getRegime();
        for (ForeignAccounts.Regime regime : ForeignAccounts.Regime.values()) {
            fx(() -> {
                g.setCurrencyRegime(regime);
                return null;
            });
            List<Choice> money = new ArrayList<>();
            money.add(policyPage("Money", "The currency"));
            money.add(stage(regime + ": the regime staged", "xr:regime", (regime.ordinal() + 1) % 4));
            money.add(stage("...the peg staged", "xr:peg", 1.4));
            money.add(choice("...the band staged", b -> {
                b.policyStage("xr:centre", 1.4);
                b.policyStage("xr:slope", .01);
                b.policyStage("xr:width", .03);
                return null;
            }));
            money.add(choice("...the payer and the aim staged", b -> { b.policyStage("xr:payer", 1); b.policyStage("xr:aim", 1); return null; }));
            money.add(stage("...the spread staged", "xr:spread", .01));
            money.add(drop("...dropped"));
            money.add(policyPage("Money", "The policy rate"));
            money.add(policyPage("Money", "Currency reform"));
            money.add(stage("the reform's factor staged", "reform", 10));
            money.add(drop("...dropped"));
            pageReshaped(g, bench, hub, 77, money, KP6_HANDED_BACK);
        }
        fx(() -> {
            g.setCurrencyRegime(regimeWas);
            bench.home(hub);
            return null;
        });

        // WHO GOES WITHOUT (Pieces.whoGoesWithout) is on the Food page and on People's: with people short of food, the card, its bar and its door.
        String[] hunger = fx(bench::hunger);
        System.out.println("  WHO GOES WITHOUT: " + hunger[0] + "; the door to the shops is " + hunger[1]);
        peopleShort(g, bench, hunger);
    }

    /**
     * People's WHO GOES SHORT list and card (sections 1 to 5 draw the page): with people short of food the card says so and draws its bar, the
     * People page kept for a stretch of months equals a page built new, and Policy's Food page, which draws the same card, equals one built new.
     * Printed, and not asserted, where the city has nobody short (the fixture cannot make the model's hunger: it is the month's).
     */
    static void peopleShort(Game g, KeptBench bench, String[] hunger) throws Exception {
        boolean short_ = !hunger[0].startsWith("nobody");
        if (!short_) {
            System.out.println("  nobody goes short of food in this city at this month: People's WHO GOES SHORT list is not exercised with someone short (the fixture cannot cause hunger; it is the month's)");
            return;
        }
        int differ = 0;
        String firstDiffer = null;
        for (int m = 1; m <= 6; m++) {
            Object[] r = fx(() -> {
                month(g);
                bench.drawKept();
                bench.passes();
                bench.finishPageAnimations();
                bench.settle();
                List<String> kept = bench.signature();
                bench.showFresh();
                bench.finishPageAnimations();
                bench.settle();
                List<String> fresh = bench.signature();
                bench.restoreKept();
                return new Object[]{firstDifference(kept, fresh), bench.hunger()[0]};
            });
            if (r[0] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[0];
            }
        }
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("People's page, with people short of food (" + hunger[0] + "), equals a page built new at six months (" + differ + " differ)", differ == 0);
    }

    /* ----- the controls (78) ----- */

    /**
     * A dial that applies at once and stages nothing (the policy rate page's target and HOW STRICT): what the model reads at the dial now, and how it is put
     * back (KP2b: the held dials were all staged ones).
     */
    record AtOnce(java.util.function.Supplier<Double> now, Runnable putBack) { }

    /** The slider at a place in a page's sliders, when it has these ends - else null (the page is not the page this expects). */
    static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> sliderAt(int index, double min, double max) {
        return all -> index < all.size() && Math.abs(all.get(index).getMin() - min) < 1e-12 && Math.abs(all.get(index).getMax() - max) < 1e-12
                ? all.get(index) : null;
    }

    /** The slider at a place among a page's sliders that run under nothing (a tax offset's dial, which reaches below zero), else null. */
    static java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> offsetDial(int index) {
        return all -> {
            List<javafx.scene.control.Slider> offsets = new ArrayList<>();
            for (javafx.scene.control.Slider s : all) if (s.getMin() < 0) offsets.add(s);
            return index < offsets.size() ? offsets.get(index) : null;
        };
    }

    /** The few frames of the code that made a change just heard by a listener (skipping this harness and the listener's own plumbing): who set it. */
    static String whoSet() {
        StringBuilder who = new StringBuilder();
        int taken = 0;
        for (StackTraceElement e : new Throwable().getStackTrace()) {
            String c = e.getClassName();
            if (taken < 6 && !c.startsWith("com.sun.javafx.binding") && !c.startsWith("javafx.beans") && !c.contains("KeptCheck")) {
                who.append(who.length() == 0 ? "" : " < ").append(c.substring(c.lastIndexOf('.') + 1)).append('.').append(e.getMethodName()).append(':').append(e.getLineNumber());
                taken++;
            }
        }
        return who.toString();
    }

    /**
     * The first sector whose zone page has a tax dial (the `index`th that runs under nothing) that moves what the ITS TAXES card says it would do when it
     * is dragged two steps, the dial put back after - or null. Not every sector pays every tax in the city as it has become.
     */
    static String taxDialMoving(KeptBench bench, int index) throws Exception {
        return fx(() -> {
            for (String sector : KeptBench.policySectors()) {
                bench.home(bench.policy("a probe of " + sector + "'s ITS TAXES", "Zoning", KeptBench.policyZonePage(sector)));
                bench.showPolicy();
                bench.passes();
                javafx.scene.control.Slider bar = offsetDial(index).apply(bench.policySliders());
                if (bar == null || bar.isDisabled()) continue;
                int before = bench.policyArrows();
                double was = bar.getValue(), step = bar.getBlockIncrement();
                bar.setValue(was + 2 * step <= bar.getMax() + 1e-9 ? was + 2 * step : was - 2 * step);
                boolean moves = bench.policyArrows() > before;
                bar.setValue(was);
                if (moves) return sector;
            }
            return null;
        });
    }

    /** What one held dial did, for holdDial to judge. */
    static final class Held {
        String none, reading0, dragged, kept, differs, dropped, firstHeard;
        boolean sameReading, sameBar, heldPressed, heldStill, heldAfter, same, tray, trayGone, stagedMid, saysDragged, saysHeld, saysCity;
        int heardText, heardStyle, control, arrowsBefore, arrowsDragged, arrowsMid;
        double shown, valueNow, thumb, cityAt, cityMid, cityAfter, cityBack, step, min;
        Double stagedAt;
    }

    /**
     * Section 78: a dial of Policy keeps the player's hand, through the screen's real show method. A draw with nothing changed first leaves the reading's
     * text and style alone (KP2b: it was set to nothing and then to its words, twice a draw). The slider is pressed and dragged two steps and a month
     * lands: the same slider node, still held, the thumb where the hand left it, the reading and the dial card's effects (the arrows of what moves) at the
     * thumb. Released, the change is staged at the thumb, the tray shows it (a dial with its own Apply has none) and the page equals a page built new; then
     * dropped, and the page is equal again. A dial that applies at once (`atOnce`) is released into the model instead and the model put back; a line
     * under the dial that says something about its step (`says`: HOW STRICT's) says it for the held step, not the city's (KP2b).
     */
    static void holdDial(Game g, KeptBench bench, String what, String area, String page,
                         java.util.function.Function<List<javafx.scene.control.Slider>, javafx.scene.control.Slider> pick,
                         String key, boolean trayShows, boolean cardEffects, AtOnce atOnce,
                         java.util.function.DoubleFunction<String> says) throws Exception {
        System.out.println("\n--- 78: the controls: " + what + " ---");
        KeptBench.Page p = fx(() -> bench.policy(what, area, page));
        Held h = fx(() -> {
            Held r = new Held();
            bench.home(p);
            bench.showPolicy();
            bench.passes();
            javafx.scene.control.Slider bar = pick.apply(bench.policySliders());
            if (bar == null) {
                StringBuilder ends = new StringBuilder();
                for (javafx.scene.control.Slider s : bench.policySliders()) ends.append(" [").append(s.getMin()).append(" to ").append(s.getMax()).append("]");
                r.none = ends.toString();
                return r;
            }
            r.step = bar.getBlockIncrement();
            double snapped = bar.getMin() + Math.round((bar.getValue() - bar.getMin()) / r.step) * r.step;
            r.thumb = snapped + 2 * r.step <= bar.getMax() + 1e-9 ? snapped + 2 * r.step : snapped - 2 * r.step;
            r.min = bar.getMin();
            r.cityAt = atOnce == null ? Double.NaN : atOnce.now().get();
            r.reading0 = KeptBench.readingOf(bar).getText();
            r.arrowsBefore = bench.policyArrows();
            // (0) a draw with nothing changed: the reading's text and style are heard no more (each is re-read in the listener, or only the first set is heard).
            javafx.scene.control.Label reading = KeptBench.readingOf(bar);
            int[] heard = {0, 0};
            javafx.beans.InvalidationListener hearText = o -> {
                if (heard[0]++ == 0) r.firstHeard = whoSet();
                ((javafx.beans.value.ObservableValue<?>) o).getValue();
            };
            javafx.beans.InvalidationListener hearStyle = o -> {
                if (heard[1]++ == 0 && r.firstHeard == null) r.firstHeard = whoSet();
                ((javafx.beans.value.ObservableValue<?>) o).getValue();
            };
            reading.textProperty().addListener(hearText);
            reading.styleProperty().addListener(hearStyle);
            bench.showPolicy();
            bench.passes();
            r.sameReading = KeptBench.readingOf(pick.apply(bench.policySliders())) == reading;
            // The counter is not deaf: a text set by hand is heard.
            r.heardText = heard[0];
            r.heardStyle = heard[1];
            reading.setText(reading.getText() + "!");
            r.control = heard[0] - r.heardText;
            reading.textProperty().removeListener(hearText);
            reading.styleProperty().removeListener(hearStyle);
            bench.showPolicy();
            bench.passes();
            // (a) pressed, dragged two steps, a month lands.
            KeptBench.press(bar);
            r.heldPressed = KeptBench.held(bar);
            bar.setValueChanging(true);
            bar.setValue(r.thumb);
            r.dragged = KeptBench.readingOf(bar).getText();
            r.arrowsDragged = bench.policyArrows();
            r.saysDragged = says == null || bench.pageSays(says.apply(r.thumb));
            month(g);
            bench.showPolicy();
            bench.passes();
            r.sameBar = pick.apply(bench.policySliders()) == bar;
            r.kept = KeptBench.readingOf(bar).getText();
            r.shown = KeptBench.shownBy(bar);
            r.heldStill = KeptBench.held(bar);
            r.valueNow = bar.getValue();
            r.arrowsMid = bench.policyArrows();
            r.stagedMid = key != null && bench.policyStaged().containsKey(key);
            r.cityMid = atOnce == null ? Double.NaN : atOnce.now().get();
            r.saysHeld = says == null || bench.pageSays(says.apply(r.thumb));
            r.saysCity = says != null && bench.pageSays(says.apply(snapped));
            // (b) released: the change staged at the thumb (or applied at once), the tray shows it, the page equals one built new.
            bar.setValueChanging(false);
            KeptBench.release(bar);
            bench.passes();
            r.heldAfter = KeptBench.held(bar);
            r.same = pick.apply(bench.policySliders()) == bar;
            r.stagedAt = key == null ? null : bench.policyStaged().get(key);
            r.cityAfter = atOnce == null ? Double.NaN : atOnce.now().get();
            r.tray = bench.policyTray() != null;
            r.differs = againstNew(bench, p);
            // (c) the staged change dropped (or the model put back): the page equal again.
            if (atOnce == null) bench.policyDrop();
            else atOnce.putBack().run();
            bench.showPolicy();
            bench.passes();
            r.dropped = againstNew(bench, p);
            r.trayGone = bench.policyTray() == null;
            r.cityBack = atOnce == null ? Double.NaN : atOnce.now().get();
            return r;
        });
        if (h.none != null) {
            System.out.println("  the page's sliders:" + h.none);
            check(what + ": the page has a dial (a slider) to hold", false);
            return;
        }
        System.out.printf("  dragged two steps: reading \"%s\" -> \"%s\"; a month later \"%s\", the ladder shows %.5f for a thumb at %.5f; effects' arrows %d -> %d -> %d%n",
                h.reading0, h.dragged, h.kept, h.shown, h.thumb, h.arrowsBefore, h.arrowsDragged, h.arrowsMid);
        check("a draw with nothing changed sets the reading's text and style no more (" + h.heardText + " of the text and " + h.heardStyle + " of the style heard, the same label: " + h.sameReading + ")",
                h.heardText == 0 && h.heardStyle == 0 && h.sameReading);
        if (h.firstHeard != null && h.heardText + h.heardStyle > 0) System.out.println("  the first change heard was set by: " + h.firstHeard);
        check("...and the counter hears a text set by hand (" + h.control + ")", h.control == 1);
        check("a slider pressed is held", h.heldPressed);
        check("a month landing on a held slider leaves the same slider node, still held", h.sameBar && h.heldStill);
        check("...its value still the thumb's (" + h.valueNow + ")", Math.abs(h.valueNow - h.thumb) < 1e-9);
        check("...the reading the thumb's words (\"" + h.kept + "\", was \"" + h.dragged + "\")", h.kept.equals(h.dragged));
        check("...Ladder.shown() the thumb's value", Math.abs(h.shown - h.thumb) < 1e-9);
        if (cardEffects) check("...the card's effects drawn at the thumb (" + h.arrowsMid + " moved rows, " + h.arrowsDragged + " while dragged)", h.arrowsMid > 0);
        if (says != null) {
            check("...the line under the dial followed the thumb while it was dragged (it says the held step's words)", h.saysDragged);
            check("...and a month landing under the hand leaves the held step's words under the thumb, not the city's step's", h.saysHeld && !h.saysCity);
        }
        if (atOnce == null) {
            check("...and nothing staged before the release", !h.stagedMid);
        } else {
            check("...and nothing applied before the release (the model at " + h.cityMid + ", was " + h.cityAt + ")", Math.abs(h.cityMid - h.cityAt) < 1e-9);
        }
        check("a slider released is not held", !h.heldAfter);
        if (atOnce == null) {
            check("...the change is staged at the thumb (" + h.stagedAt + ")", h.stagedAt != null && Math.abs(h.stagedAt - h.thumb) < 1e-6);
        } else {
            check("...the change is applied at once, the model at the thumb (" + h.cityAfter + ")", Math.abs(h.cityAfter - h.thumb) < 1e-6);
        }
        check(trayShows ? "...the tray shows it" : "...(this dial keeps its own Apply, or applies at once: no tray)", h.tray == trayShows);
        check("...and the page is the same slider, kept, equal to a page built new", h.same && h.differs == null);
        if (h.differs != null) System.out.println("  " + h.differs);
        check(atOnce == null ? "the staged change dropped: the tray is gone and the page equal to a page built new"
                : "the model put back (" + h.cityBack + "): the page equal to a page built new", h.trayGone && h.dropped == null
                && (atOnce == null || Math.abs(h.cityBack - h.cityAt) < 1e-9));
        if (h.dropped != null) System.out.println("  " + h.dropped);
    }

    /** Section 78's dial, held, the first slider of a page: see holdDial. */
    static void policyDial(Game g, KeptBench bench, String what, String area, String page, String key, boolean trayShows) throws Exception {
        holdDial(g, bench, what, area, page, all -> all.isEmpty() ? null : all.get(0), key, trayShows, true, null, null);
    }

    /**
     * Section 78's last part: a card comes in front of a dial the player holds. Every dial card is a scope keyed by what it is for (a payer, a
     * course), so the cards after the new one keep their nodes: the held slider is the same node, at the same thumb, with its own reading and its own
     * ladder, and cards kept by order alone would hand its node to the card in front (the page then shows the held thumb on another lever's dial).
     * `setup` puts the page as it is before the hold; `front` is what puts a card in front of the last dial, with a month landing as it does.
     */
    static void heldWithCardInFront(Game g, KeptBench bench, String what, KeptBench.Page p, Hand setup, Hand front) throws Exception {
        Object[] r = fx(() -> {
            bench.home(p);
            String refused = setup.on(bench);
            if (refused != null) return new Object[]{"the page could not be set: " + refused};
            bench.showPolicy();
            bench.passes();
            List<javafx.scene.control.Slider> before = bench.policySliders();
            if (before.size() < 2) return new Object[]{"the page has " + before.size() + " dials"};
            javafx.scene.control.Slider bar = before.get(before.size() - 1);
            double step = bar.getBlockIncrement();
            double snapped = bar.getMin() + Math.round((bar.getValue() - bar.getMin()) / step) * step;
            double thumb = snapped + 2 * step <= bar.getMax() + 1e-9 ? snapped + 2 * step : snapped - 2 * step;
            String was = KeptBench.readingOf(bar).getText();
            KeptBench.press(bar);
            bar.setValueChanging(true);
            bar.setValue(thumb);
            String dragged = KeptBench.readingOf(bar).getText();
            // A card comes in front of it, and a month lands.
            refused = front.on(bench);
            if (refused != null) return new Object[]{"the card could not be put in front: " + refused};
            month(g);
            bench.showPolicy();
            bench.passes();
            List<javafx.scene.control.Slider> after = bench.policySliders();
            boolean grew = after.size() == before.size() + 1;
            boolean sameBar = !after.isEmpty() && after.get(after.size() - 1) == bar;
            boolean heldStill = KeptBench.held(bar);
            double valueNow = bar.getValue();
            String reading = KeptBench.readingOf(bar).getText();
            double shown = KeptBench.shownBy(bar);
            // The dials in front of it are theirs: none of them carries the held thumb.
            boolean othersOwn = true;
            for (javafx.scene.control.Slider other : after) {
                if (other != bar && Math.abs(other.getValue() - thumb) < 1e-12 && other.getMax() == bar.getMax() && other.getMin() == bar.getMin()) othersOwn = false;
            }
            bar.setValueChanging(false);
            KeptBench.release(bar);
            bench.passes();
            String differs = againstNew(bench, p);
            bench.policyDrop();
            bench.home(p);
            bench.showPolicy();
            bench.passes();
            String dropped = againstNew(bench, p);
            return new Object[]{null, grew, before.size(), after.size(), sameBar, heldStill, valueNow, thumb, reading, dragged, was, shown, differs, dropped, othersOwn};
        });
        if (r[0] != null) {
            check(what + ": " + r[0], false);
            return;
        }
        System.out.printf("  %s: the last dial dragged from %s to %s, a card put in front (%d dials, then %d), a month landed: it reads %s%n", what, r[10], r[9], r[2], r[3], r[8]);
        check(what + ": a dial card comes in front of the held dial (the page has one dial more: " + r[2] + ", then " + r[3] + ")", (boolean) r[1]);
        check("...the held slider is the same node, still held, at the thumb (" + r[6] + ")", (boolean) r[4] && (boolean) r[5] && Math.abs((double) r[6] - (double) r[7]) < 1e-9);
        check("...its reading the thumb's (\"" + r[8] + "\", dragged to \"" + r[9] + "\") and Ladder.shown() the thumb's value", r[8].equals(r[9]) && Math.abs((double) r[11] - (double) r[7]) < 1e-9);
        check("...and no dial in front of it carries its thumb", (boolean) r[14]);
        check("...released, the page is equal to a page built new", r[12] == null);
        if (r[12] != null) System.out.println("  " + r[12]);
        check("...the card taken away and the change dropped: the page is equal to a page built new", r[13] == null);
        if (r[13] != null) System.out.println("  " + r[13]);
    }

    /** Section 78's last part: a card in front of a held dial, on the Schools page (a course's own price in front of the loan's) and on a tax page (a payer's own move in front of another's). */
    static void policyDialsKeep(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 78: a card comes in front of a held dial ---");
        KeptBench.Page schools = fx(() -> bench.policy("Policy, Promises' Schools page", "Promises", "Schools"));
        heldWithCardInFront(g, bench, "the Schools page", schools, b -> null, b -> { b.policyLine("course:UNIVERSITY", true); return null; });
        // The first tax with two payers that show a row and a dial of their own (in a city where one sector pays the profit tax, the sales tax has more).
        String found = null;
        List<String> payers = List.of();
        for (String tax : List.of("Profit", "Sales", "Property", "Wage")) {
            List<String> of = fx(() -> bench.policyPayers(tax));
            System.out.println("  " + tax + "'s payers with a row and a dial of their own, top of the ranking first: " + of);
            if (found == null && of.size() >= 2) {
                found = tax;
                payers = of;
            }
        }
        check("a tax has two payers with a row and a dial of their own to open (" + found + ")", found != null);
        if (found != null) {
            final String tax = found;
            final List<String> two = payers;
            KeptBench.Page page = fx(() -> bench.policy("Policy, Taxes' " + tax + " page", "Taxes", tax));
            heldWithCardInFront(g, bench, "the " + tax + " page", page, b -> { b.policyLine("payer:" + two.get(1), true); return null; },
                    b -> { b.policyLine("payer:" + two.get(0), true); return null; });
        }
    }

    /** Section 78: the controls on Policy - a tax page's rate dial, the policy rate's, a zone's ground dial, and a card coming in front of a held dial. */
    static void kp6Controls(Game g, KeptBench bench) throws Exception {
        policyDial(g, bench, "a tax page's rate dial", "Taxes", "Profit", "base:profit", true);
        policyDial(g, bench, "the policy rate's dial", "Money", "The policy rate", "policy", false);
        // KP2b: the dials that apply at once, on the policy rate page after its own (the target's ladder, then HOW STRICT's, whose line under it says what
        // the rule would set at the step under the thumb), held and released into the model, which is put back after.
        double[] was = fx(() -> new double[]{g.getDebtManager().getInflationTarget(), g.getDebtManager().getStrictness().ordinal()});
        int strictTop = DebtManager.Strictness.values().length - 1;
        holdDial(g, bench, "the policy rate page's target ladder", "Money", "The policy rate",
                sliderAt(1, DebtManager.MIN_INFLATION_TARGET, DebtManager.MAX_INFLATION_TARGET), null, false, false,
                new AtOnce(() -> g.getDebtManager().getInflationTarget(), () -> g.getDebtManager().setInflationTarget(was[0])), null);
        holdDial(g, bench, "HOW STRICT on the policy rate page", "Money", "The policy rate", sliderAt(2, 0, strictTop), null, false, false,
                new AtOnce(() -> (double) g.getDebtManager().getStrictness().ordinal(),
                        () -> g.getDebtManager().setStrictness(DebtManager.Strictness.at((int) was[1]))),
                v -> KeptBench.strictLine(g, v));
        boolean zoningOpen = fx(() -> g.isLeverOpen(PolicyDates.Lever.ZONING));
        if (zoningOpen) {
            policyDial(g, bench, "a zone's ground dial", "Zoning", KeptBench.policyZonePage(Sectors.AGRICULTURE), "zone:" + Sectors.AGRICULTURE + ":size", true);
            // KP2b: a card of several dials, a dial that is not its first - ITS TAXES, whose second dial is the sales tax's while that lever is open
            // (the property tax's, the third, when not), on the first sector whose card says something would move when it is dragged (a sector that
            // pays no sales tax in this city moves nothing, and a held dial with nothing to show proves nothing about the card).
            boolean salesOpen = fx(() -> g.isLeverOpen(PolicyDates.Lever.SALES_TAX));
            Zoning.Tax tax = salesOpen ? Zoning.Tax.SALES : Zoning.Tax.PROPERTY;
            String sector = taxDialMoving(bench, tax.ordinal());
            check("a sector's ITS TAXES card has a " + tax.name().toLowerCase() + " dial that moves what it would do (" + sector + ")", sector != null);
            if (sector != null) {
                holdDial(g, bench, "a zone's ITS TAXES (" + sector + "), its " + (salesOpen ? "second dial (sales)" : "third dial (property; sales is shut)"), "Zoning",
                        KeptBench.policyZonePage(sector), offsetDial(tax.ordinal()), KeptBench.policyZoneTaxKey(tax, sector), true, true, null, null);
            }
        } else {
            System.out.println("  zoning is shut in this city: a zone's dial is greyed and stages nothing (not reached by a fixture)");
        }
        policyDialsKeep(g, bench);
    }

    /* ----- the count-up (79) ----- */

    /** Whether a counted figure's words moved between last month and this. */
    private static boolean moved(String[] words) {
        return words[0] != null && !words[0].equals(words[1]);
    }

    /**
     * One clock speed of section 79 on a Trade page: the page is shown through its real show method, a month that moved SOLD ABROAD or BOUGHT ABROAD
     * lands and it is shown again; each of the strip's two figures that moved starts a count, and the bars (on the pages that have them) grow, exactly
     * where `expect` says. Where nothing counts, nothing runs, each figure reads this month's words at once, and the page is the page built new.
     * Null, or what was wrong.
     */
    static String tradeRung(Game g, KeptBench bench, String page, boolean bars, int at, boolean paused, boolean expect) throws Exception {
        bench.clock(!paused, null);
        bench.speed(at);
        bench.tradeOpen(page);
        bench.showTrade();
        bench.finishPageAnimations();
        bench.passes();
        List<javafx.scene.control.Label> figures = bench.tradeFigures();
        if (figures.size() != 2) return "the strip has no two figures to count (" + figures.size() + ")";
        List<Animation> before = new ArrayList<>();
        for (javafx.scene.control.Label f : figures) before.add(KeptBench.countOf(f));
        List<? extends javafx.scene.Node> mirrors = bench.tradeBars();
        List<Animation> mirrorsBefore = new ArrayList<>();
        for (javafx.scene.Node m : mirrors) mirrorsBefore.add(KeptBench.countOf(m));
        if (!landUntil(g, () -> {
            String[][] w = bench.tradeWords();
            return moved(w[0]) || moved(w[1]);
        })) return "no month in twelve moved SOLD ABROAD or BOUGHT ABROAD";
        bench.showTrade();
        bench.passes();
        String[][] words = bench.tradeWords();
        StringBuilder bad = new StringBuilder();
        if (!bench.tradeFigures().equals(figures)) bad.append("the strip's figures were made new; ");
        for (int i = 0; i < 2; i++) {
            javafx.scene.control.Label f = figures.get(i);
            boolean started = KeptBench.countOf(f) != before.get(i) && KeptBench.counting(f);
            boolean want = expect && moved(words[i]);
            if (started != want) bad.append(i == 0 ? "SOLD ABROAD " : "BOUGHT ABROAD ").append(want ? "did not count" : "counted").append("; ");
            if (!expect && !f.getText().equals(words[i][1])) bad.append("a figure reads \"").append(f.getText()).append("\" at once, not this month's; ");
        }
        List<? extends javafx.scene.Node> mirrorsNow = bench.tradeBars();
        if (bars) {
            if (mirrorsNow.isEmpty()) bad.append("the page has no bars to grow; ");
            for (int i = 0; i < mirrorsNow.size(); i++) {
                boolean grew = i < mirrors.size() && mirrorsNow.get(i) == mirrors.get(i) ? KeptBench.countOf(mirrorsNow.get(i)) != mirrorsBefore.get(i) && KeptBench.counting(mirrorsNow.get(i))
                        : KeptBench.counting(mirrorsNow.get(i));
                if (grew != expect) bad.append(expect ? "the bars did not grow" : "the bars grew").append("; ");
            }
        }
        if (!expect) {
            // Nothing counts, so the page is already the page a month's figures draw: equal to a page built new, with no animation finished first.
            bad.append(stillOrNot(bench));
            KeptBench.Page p = bench.trade("the page", page);
            String d = againstNewAsIs(bench, p);
            if (d != null) bad.append("not the page built new: ").append(d.replace('\n', ' ')).append("; ");
        } else {
            bench.finishPageAnimations();
            bench.passes();
            for (int i = 0; i < 2; i++) {
                if (!figures.get(i).getText().equals(words[i][1])) {
                    bad.append("a figure reads \"").append(figures.get(i).getText()).append("\" after its count, not \"").append(words[i][1]).append("\"; ");
                }
            }
        }
        return bad.length() == 0 ? null : "at " + (paused ? "a paused clock" : KeptBench.speedLadder()[at] + "x") + " on " + page + ": " + bad;
    }

    /**
     * One clock speed on a page that has no animation to run (a Government or Policy page): a month lands and the page is shown through its real
     * show method; nothing starts at any rung, nothing is running or off its rest, and where the clock is too fast to count, the page is the page built new.
     */
    static String quietRung(Game g, KeptBench bench, KeptBench.Page page, Runnable open, Runnable show, int at, boolean paused, boolean expect) throws Exception {
        bench.clock(!paused, null);
        bench.speed(at);
        open.run();
        show.run();
        bench.finishPageAnimations();
        bench.passes();
        java.util.Set<Animation> before = animationsNow(bench);
        month(g);
        show.run();
        bench.passes();
        int[] s = started(bench, before);
        StringBuilder bad = new StringBuilder();
        if (s[0] + s[1] + s[2] > 0) bad.append(s[0] + s[1] + s[2]).append(" animations started; ");
        bad.append(stillOrNot(bench));
        if (!expect) {
            String d = againstNewAsIs(bench, page);
            if (d != null) bad.append("not the page built new: ").append(d.replace('\n', ' ')).append("; ");
        }
        return bad.length() == 0 ? null : "at " + (paused ? "a paused clock" : KeptBench.speedLadder()[at] + "x") + " on " + page.name() + ": " + bad;
    }

    /**
     * Section 79: the count-up only at 1x-10x. Trade's strip counts SOLD ABROAD and BOUGHT ABROAD up from last month's and its bars (the Overview's
     * and What we trade's) grow out of their axis when a month lands: with the clock at each rung of the speed ladder, and paused, exactly at the
     * rungs UserInterface.countsUp() allows (COUNT_UP_TOP); past it nothing animates, each figure reads this month's words and the page is the
     * page built new; a count still running when a month lands past it is stopped. The Government tab and Policy have no month animation: none
     * starts at any rung.
     */
    static void kp6CountUp(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 79: the count-up only at 1x-10x (Trade; Government and Policy have none) ---");
        double[] ladder = KeptBench.speedLadder();
        double top = KeptBench.countUpTop();
        int countingRungs = 0;
        for (double speed : ladder) if (speed <= top) countingRungs++;
        System.out.printf("  the ladder has %d rungs, %d of them at or under COUNT_UP_TOP (%.0fx)%n", ladder.length, countingRungs, top);
        List<String> names = KeptBench.tradePages();
        String[][] views = {{names.get(0), "bars", "the Overview's SOLD and BOUGHT counts and its bars' growth"},
                {names.get(2), "bars", "What we trade's counts and its bars' growth"}, {names.get(1), "", "the Month page's SOLD and BOUGHT counts"}};
        for (String[] view : views) {
            StringBuilder wrong = new StringBuilder();
            fx(() -> {
                bench.forgetTrade();
                return null;
            });
            for (int rung = 0; rung <= ladder.length; rung++) {
                // The last pass is the clock stopped, at the top rung: a month stepped by hand counts at any speed.
                boolean paused = rung == ladder.length;
                int at = paused ? ladder.length - 1 : rung;
                boolean expect = paused || ladder[at] <= top;
                String result = fx(() -> tradeRung(g, bench, view[0], !view[1].isEmpty(), at, paused, expect));
                if (result != null) wrong.append(wrong.length() == 0 ? "" : "; ").append(result);
            }
            if (wrong.length() > 0) System.out.println("  " + view[2] + ": " + wrong);
            check("a month landing starts " + view[2] + " exactly at the rungs countsUp() allows, and when paused (" + (ladder.length + 1) + " clocks)", wrong.length() == 0);

            // A count running when the clock has been sped past the top: the next month sets the figures at once and stops it.
            String stopped = fx(() -> {
                int slow = 0;
                while (ladder[slow] > top) slow++;
                bench.clock(true, null);
                bench.speed(slow);
                bench.tradeOpen(view[0]);
                bench.showTrade();
                bench.finishPageAnimations();
                bench.passes();
                if (!landUntil(g, () -> {
                    String[][] w = bench.tradeWords();
                    return moved(w[0]) || moved(w[1]);
                })) return "no month in twelve moved the figures";
                bench.showTrade();
                bench.passes();
                List<javafx.scene.control.Label> figures = bench.tradeFigures();
                boolean counting = false;
                for (javafx.scene.control.Label f : figures) counting |= KeptBench.counting(f);
                List<? extends javafx.scene.Node> mirrors = bench.tradeBars();
                for (javafx.scene.Node m : mirrors) counting |= KeptBench.counting(m);
                if (!counting) return "no count or growth was running to stop";
                List<Animation> running = new ArrayList<>(bench.pageAnimations());
                bench.speed(ladder.length - 1);
                if (!landUntil(g, () -> {
                    String[][] w = bench.tradeWords();
                    return moved(w[0]) || moved(w[1]);
                })) return "no month in twelve moved the figures";
                bench.showTrade();
                bench.passes();
                String[][] words = bench.tradeWords();
                java.util.Set<Animation> onPage = animationsNow(bench);
                for (Animation a : running) {
                    if (onPage.contains(a) && a.getStatus() != Animation.Status.STOPPED) return "an animation is still " + a.getStatus();
                }
                String bad = stillOrNot(bench);
                if (!bad.isEmpty()) return bad;
                for (int i = 0; i < 2; i++) {
                    if (!figures.get(i).getText().equals(words[i][1])) return "a figure reads \"" + figures.get(i).getText() + "\", not \"" + words[i][1] + "\"";
                }
                String d = againstNewAsIs(bench, bench.trade("the page", view[0]));
                return d == null ? null : "the page is not the page built new: " + d.replace('\n', ' ');
            });
            if (stopped != null) System.out.println("  " + view[2] + ": " + stopped);
            check("a count running when a month lands past COUNT_UP_TOP is stopped on " + view[2] + ", and the page is the month's", stopped == null);
        }

        // Government and Policy: no month animation. Nothing starts at any rung, and past the gate the page is the month's.
        record Quiet(String name, KeptBench.Page page, Runnable open, Runnable show) { }
        List<Quiet> quiet = new ArrayList<>();
        for (String p : KeptBench.governmentPages()) {
            quiet.add(new Quiet("the Government " + p + " page", fx(() -> bench.government("the Government " + p + " page", p)),
                    () -> bench.governmentOpen(p), bench::showGovernment));
        }
        quiet.add(new Quiet("the Policy hub", fx(bench::policy), () -> bench.policyOpen(null, null), bench::showPolicy));
        for (String[] ap : new String[][] {{"Taxes", "Profit"}, {"Money", "The policy rate"}, {"Zoning", "The ground"}}) {
            quiet.add(new Quiet("the Policy " + ap[0] + " › " + ap[1] + " page", fx(() -> bench.policy("the Policy " + ap[0] + " › " + ap[1] + " page", ap[0], ap[1])),
                    () -> bench.policyOpen(ap[0], ap[1]), bench::showPolicy));
        }
        for (Quiet q : quiet) {
            StringBuilder wrong = new StringBuilder();
            fx(() -> {
                bench.forgetGovernment();
                bench.forgetPolicy();
                return null;
            });
            for (int rung = 0; rung <= ladder.length; rung++) {
                boolean paused = rung == ladder.length;
                int at = paused ? ladder.length - 1 : rung;
                boolean expect = paused || ladder[at] <= top;
                String result = fx(() -> quietRung(g, bench, q.page(), q.open(), q.show(), at, paused, expect));
                if (result != null) wrong.append(wrong.length() == 0 ? "" : "; ").append(result);
            }
            if (wrong.length() > 0) System.out.println("  " + q.name() + ": " + wrong);
            check(q.name() + ": a month landing starts no animation at any of the " + (ladder.length + 1) + " clocks, and past COUNT_UP_TOP the page is the month's", wrong.length() == 0);
        }
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
    }

    /* ----- the hover (80) and the cost (81) ----- */

    /** The pointer put over a node of each kind on a page (its `setup` made first), hoverLine() saying what came of it; `found` notes where a node was found. */
    private static void hoverKP6(Game g, KeptBench bench, java.util.Map<String, Boolean> found, String where, KeptBench.Page page, Hand setup, String... kinds) throws Exception {
        for (String kind : kinds) {
            String d = fx(() -> {
                bench.home(page);
                String refused = setup.on(bench);
                if (refused != null) return refused;
                bench.drawKept(page);
                bench.passes();
                return bench.hoverKept(kind, () -> bench.drawKept(page));
            });
            hoverLine(found, where, kind, d);
            found.merge(where + "|" + kind, !KeptBench.ABSENT.equals(d), Boolean::logicalOr);
        }
    }

    /**
     * Section 80: the hover. On the Government tab a door, a status cell, a key row of the split ring, a bridge row and a ranked bar's row; on Trade a
     * door card, a mirror row's ground and a status cell; on Policy an area card and the tall rows of a payer, a course and a zone: a pointer put over
     * the node and the page drawn kept again leave the same node in the look its own mouse-entered gives it, and with the pointer off, the look its
     * mouse-exited gives it.
     */
    static void kp6Hover(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 80: the hover is kept (Government, Trade, Policy) ---");
        java.util.Map<String, Boolean> found = new java.util.LinkedHashMap<>();
        Hand none = b -> null;
        List<String> trade = KeptBench.tradePages();
        KeptBench.Page govOverview = fx(bench::government);
        KeptBench.Page govRevenue = fx(() -> bench.government("the Government Revenue page", "Revenue"));
        KeptBench.Page tradeOverview = fx(bench::trade);
        KeptBench.Page tradeGoods = fx(() -> bench.trade("the Trade tab's " + trade.get(2) + " page", trade.get(2)));
        KeptBench.Page hub = fx(bench::policy);
        KeptBench.Page profit = fx(() -> bench.policy("Policy, Taxes' Profit page", "Taxes", "Profit"));
        KeptBench.Page schools = fx(() -> bench.policy("Policy, Promises' Schools page", "Promises", "Schools"));
        KeptBench.Page ground = fx(() -> bench.policy("Policy, Zoning's ground", "Zoning", "The ground"));
        hoverKP6(g, bench, found, "the Government Overview", govOverview, none, "door", "limitCell", "ringKeyRow", "bridgeRow");
        hoverKP6(g, bench, found, "the Government Revenue page", govRevenue, none, "rankRow");
        hoverKP6(g, bench, found, "the Trade Overview", tradeOverview, none, "doorCard", "mirrorGround", "limitCell");
        hoverKP6(g, bench, found, "the Trade tab's " + trade.get(2) + " page", tradeGoods, none, "mirrorGround");
        hoverKP6(g, bench, found, "the Policy hub", hub, none, "doorCard", "limitCell");
        hoverKP6(g, bench, found, "Policy's Taxes › Profit page", profit, none, "policyRow");
        hoverKP6(g, bench, found, "Policy's Promises › Schools page", schools, none, "policyRow");
        boolean zones = !fx(bench::policyZoned).isEmpty();
        if (zones) hoverKP6(g, bench, found, "Policy's Zoning › The ground page", ground, none, "policyRow");
        else System.out.println("  no zone stands in this city: the ground's rows are not reached by a fixture");
        for (String where : List.of("the Government Overview|door", "the Government Overview|limitCell", "the Government Overview|ringKeyRow", "the Government Overview|bridgeRow",
                "the Government Revenue page|rankRow", "the Trade Overview|doorCard", "the Trade Overview|mirrorGround", "the Trade Overview|limitCell",
                "the Policy hub|doorCard", "Policy's Taxes › Profit page|policyRow", "Policy's Promises › Schools page|policyRow")) {
            check(where.replace("|", ": a ") + " was found on the page of this city, to put the pointer on", found.getOrDefault(where, false));
        }
        if (zones) check("Policy's Zoning › The ground page: a policyRow was found on the page of this city, to put the pointer on", found.getOrDefault("Policy's Zoning › The ground page|policyRow", false));
    }

    /**
     * Section 80's last part: the tooltips are kept. A node a kept draw hands back keeps its own Tooltip (Kept.tip tells it this month's words and delay);
     * a tooltip made new by Tooltip.install on every draw would replace the one the player may be reading when a month lands. So across a month's
     * redraw, every node that had a tooltip and still has one has the same Tooltip object, on every page of the three screens (Trade's river too).
     */
    static void kp6Tips(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 80: the tooltips are kept (Government, Trade, Policy) ---");
        record Where(String screen, String name, KeptBench.Page page, Hand setup) { }
        List<Where> where = new ArrayList<>();
        for (String p : KeptBench.governmentPages()) {
            where.add(new Where("Government", "the Government " + p + " page", fx(() -> bench.government("the Government " + p + " page", p)), b -> null));
        }
        List<String> trade = KeptBench.tradePages();
        for (String p : trade) where.add(new Where("Trade", "the Trade tab's " + p + " page", fx(() -> bench.trade("the Trade tab's " + p + " page", p)), b -> null));
        where.add(new Where("Trade", "the Trade tab's " + trade.get(1) + " page as the river", fx(() -> bench.trade("the river", trade.get(1))),
                b -> { b.tradeRiver(true); return null; }));
        where.add(new Where("Trade", "the Trade tab's " + trade.get(2) + " page by business", fx(() -> bench.trade("by business", trade.get(2))),
                b -> { b.tradeByBusiness(true); return null; }));
        where.add(new Where("Trade", "the Trade tab's " + trade.get(3) + " page", fx(() -> bench.trade("the Partners table of the year", trade.get(3))),
                b -> { b.tradeSpan(true); return null; }));
        where.add(new Where("Policy", "the Policy hub", fx(bench::policy), b -> null));
        for (String[] ap : new String[][] {{"Taxes", "Profit"}, {"Money", "The policy rate"}, {"Promises", "Schools"}, {"Zoning", "The ground"}}) {
            where.add(new Where("Policy", "Policy's " + ap[0] + " › " + ap[1] + " page", fx(() -> bench.policy("Policy's " + ap[0] + " › " + ap[1] + " page", ap[0], ap[1])), b -> null));
        }
        java.util.Map<String, int[]> perScreen = new java.util.LinkedHashMap<>();
        for (Where w : where) {
            int[] c = fx(() -> {
                bench.home(w.page());
                w.setup().on(bench);
                bench.drawKept(w.page());
                bench.passes();
                java.util.Map<javafx.scene.Node, javafx.scene.control.Tooltip> before = bench.tooltips();
                month(g);
                bench.drawKept(w.page());
                bench.passes();
                java.util.Map<javafx.scene.Node, javafx.scene.control.Tooltip> after = bench.tooltips();
                java.util.Set<javafx.scene.Node> still = bench.nodesOnPage();
                int both = 0, same = 0, lost = 0;
                for (java.util.Map.Entry<javafx.scene.Node, javafx.scene.control.Tooltip> e : before.entrySet()) {
                    javafx.scene.control.Tooltip now = after.get(e.getKey());
                    if (now == null) {
                        // (A node kept on the page that has no tooltip now lost the one it had: Kept takes the tooltip off a kept node its draw does not tip.)
                        if (still.contains(e.getKey())) lost++;
                        continue;
                    }
                    both++;
                    if (now == e.getValue()) same++;
                }
                return new int[]{before.size(), both, same, lost};
            });
            int[] sum = perScreen.computeIfAbsent(w.screen(), k -> new int[4]);
            for (int i = 0; i < 4; i++) sum[i] += c[i];
            System.out.printf("  %s: %d tooltips, %d of them on a node that has one after a month's redraw, %d the same Tooltip, %d on a node kept without one%n", w.name(), c[0], c[1], c[2], c[3]);
            check(w.name() + ": every tooltip a node had and still has is the same Tooltip after a month's kept redraw (" + c[2] + " of " + c[1] + "), and none is lost from a node the page keeps (" + c[3] + ")",
                    c[1] == c[2] && c[3] == 0);
        }
        for (java.util.Map.Entry<String, int[]> e : perScreen.entrySet()) {
            check("the " + e.getKey() + " pages carry tooltips that a month leaves on their nodes, to be kept (" + e.getValue()[1] + ")", e.getValue()[1] > 0);
        }
    }

    /** Section 81: what the kept month costs on the Government Overview, on Trade's Overview and its heaviest page, and on Policy's hub and its dial-heavy pages. */
    static void kp6Cost(Game g, KeptBench bench) throws Exception {
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        pageCost(g, bench, fx(bench::government), 81, KEPT_GOVERNMENT_MS, "KEPT_GOVERNMENT_MS");

        List<KeptBench.Page> tradePages = new ArrayList<>();
        for (String p : KeptBench.tradePages()) tradePages.add(fx(() -> bench.trade("the Trade tab's " + p + " page", p)));
        pageCost(g, bench, tradePages.get(0), 81, KEPT_TRADE_MS, "KEPT_TRADE_MS");
        KeptBench.Page heavy = heaviest(bench, "Trade", tradePages);
        if (heavy != tradePages.get(0)) pageCost(g, bench, heavy, 81, KEPT_TRADE_MS, "KEPT_TRADE_MS");

        pageCost(g, bench, fx(bench::policy), 81, KEPT_POLICY_MS, "KEPT_POLICY_MS");
        pageCost(g, bench, fx(() -> bench.policy("Policy, Taxes' Profit page", "Taxes", "Profit")), 81, KEPT_POLICY_MS, "KEPT_POLICY_MS");
        pageCost(g, bench, fx(() -> bench.policy("Policy, Money's policy rate page", "Money", "The policy rate")), 81, KEPT_POLICY_MS, "KEPT_POLICY_MS");
    }

    /** Sections 69 to 81: Government, Trade & the World and Policy kept. */
    static void screensKP6(Game g, KeptBench bench) throws Exception {
        System.out.printf("%n=== Government, Trade & the World and Policy (KP6): a city at month %d ===%n", g.getMonth());
        long t = System.nanoTime();
        int c = checks;
        kp6Government(g, bench);
        t = tookWith("the Government tab (69 to 71)", t, c);
        c = checks;
        kp6Trade(g, bench);
        t = tookWith("the Trade tab (72 to 74)", t, c);
        c = checks;
        kp6Policy(g, bench);
        t = tookWith("the Policy tab (75 to 77)", t, c);
        c = checks;
        kp6Controls(g, bench);
        t = tookWith("the controls (78)", t, c);
        c = checks;
        kp6CountUp(g, bench);
        t = tookWith("the count-up (79)", t, c);
        c = checks;
        kp6Hover(g, bench);
        kp6Tips(g, bench);
        t = tookWith("the hover and the tooltips (80)", t, c);
        c = checks;
        kp6Cost(g, bench);
        tookWith("the cost (81)", t, c);
        // What the sections put in the city is taken out: the zones.
        fx(() -> {
            for (String s : bench.policyZoned()) bench.policyUnzone(s);
            return null;
        });
    }

    /* =====================================================================
       THE RESEARCH AREA (R5, slice B1), section 95: the Found page kept, and what City History's flags and the stacking of the timeline are made of
       ===================================================================== */

    /** Whether some line of a signature has all of these words in it. */
    static boolean has(List<String> signature, String... words) {
        for (String line : signature) {
            boolean all = true;
            for (String w : words) all &= line.contains(w);
            if (all) return true;
        }
        return false;
    }

    /** A label's line in a signature: `Label "text"`. */
    static String label(String text) {
        return "Label \"" + text + "\"";
    }

    /**
     * Section 95: Policy > Research > Found, the sixth Policy area's page, kept (R5, slice B1). In a city founded the default way it is one quiet card;
     * on ResearchViewCheck.researchTown() (a find here, two from abroad, an unfitted known piece, a new template, a recipe in use) the page drawn kept
     * over last month's nodes is the page built new, month after month and after what reshapes it, and it shows the lead, a FOUND HERE row, a recipe's
     * row, a piece's row (a door to its sector's Investors page) and four charts. Then the pure helpers its timeline and City History's flags are made
     * of: two dots a pixel apart stack and two far apart do not, the year ticks, the flag's counts, the discovery's own colour and the lists of
     * decisions that leave the discoveries out.
     */
    static void researchFound(Game g, KeptBench defaultBench) throws Exception {
        System.out.printf("%n=== The Research area's Found page (R5): the default city at month %d and a city founded the 1900 way ===%n", g.getMonth());
        long t = System.nanoTime();
        int c = checks;

        System.out.println("\n--- 95: in a city founded the default way the page is one quiet card ---");
        boolean walked = false;
        for (String[] p : KeptBench.policyPages()) walked |= "Research".equals(p[0]) && "Found".equals(p[1]);
        check("the Research area's Found page is one of the fixed pages section 77's default bench walks (KeptBench.policyPages())", walked);
        KeptBench.Page quiet = fx(() -> defaultBench.policy("Policy, Research's Found page in a default city", "Research", "Found"));
        List<String> quietPage = fx(() -> {
            defaultBench.home(quiet);
            defaultBench.drawKept(quiet);
            defaultBench.passes();
            defaultBench.settle();
            return defaultBench.signature();
        });
        check("the default city's page is the one card that says nothing is found (" + ResearchView.knownAtFounding(g) + " known at the founding)",
                has(quietPage, label("NOTHING TO FIND")) && has(quietPage, "discoveries in the catalogue, so nothing arrives from abroad")
                        && !has(quietPage, label("THE TIMELINE")) && !has(quietPage, label("FOUND HERE")));
        check("...and City History draws no discovery flag there", ResearchView.flagEntries(g).isEmpty());

        Game town = fx(ResearchViewCheck::researchTown);
        KeptBench bench = fx(() -> new KeptBench(town));
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        KeptBench.Page found = fx(() -> bench.policy("Policy, Research's Found page", "Research", "Found"));
        pageKept(town, bench, found, 95);
        // KP2b's rule (section 92's nothingSet()) on the page with something on it, which section 92's default bench cannot show: a second kept draw
        // in the same month writes no style, text, graphic, padding or width.
        nothingSet(bench, found, "Policy, Research, Found in the town (its timeline, rows and charts)");
        arrive(bench, found);
        pageShows(town, bench, found, 95);

        System.out.println("\n--- 95: what the page shows, from the model's own reads ---");
        Object[] seen = fx(() -> {
            bench.home(found);
            bench.drawKept(found);
            bench.passes();
            bench.settle();
            return new Object[]{bench.signature(), ResearchView.timeline(town), ResearchView.recipesInUse(town), ResearchView.equipment(town, null),
                    bench.researchDots()};
        });
        @SuppressWarnings("unchecked") List<String> sig = (List<String>) seen[0];
        @SuppressWarnings("unchecked") List<ResearchView.Discovery> timeline = (List<ResearchView.Discovery>) seen[1];
        @SuppressWarnings("unchecked") List<ResearchView.RecipeUse> recipes = (List<ResearchView.RecipeUse>) seen[2];
        @SuppressWarnings("unchecked") List<ResearchView.PieceRow> pieces = (List<ResearchView.PieceRow>) seen[3];
        @SuppressWarnings("unchecked") List<double[]> dots = (List<double[]>) seen[4];
        int here = ResearchView.count(timeline, ResearchView.Source.HERE), abroad = ResearchView.count(timeline, ResearchView.Source.ABROAD);
        check("fixture: the town has a find here, discoveries from abroad, a recipe in use and a piece of equipment (" + here + " here, " + abroad
                + " abroad, " + recipes.size() + " recipes, " + pieces.size() + " pieces)", here >= 1 && abroad >= 2 && !recipes.isEmpty() && !pieces.isEmpty());
        check("the lead counts them: \"" + here + " found here, " + abroad + " from abroad since the founding\"",
                has(sig, label(here + " found here, " + abroad + " from abroad since the founding")));

        boolean rows = true, tips = true;
        for (ResearchView.Discovery d : timeline) {
            String years = String.format(java.util.Locale.ROOT, "%.1f", d.yearsAhead());
            String when = d.source() == ResearchView.Source.HERE
                    ? "found here in " + CityCalendar.format(d.month()) + ", " + years + " years before the world"
                    : "from abroad, " + CityCalendar.format(d.month());
            tips &= has(sig, "tip{" + d.entry().name() + "\n", when);
            if (d.source() == ResearchView.Source.HERE) rows &= has(sig, label(d.entry().name())) && has(sig, label(years + " years before the world"));
        }
        check("every discovery is a dot whose tooltip names it and says how the city came to know it, in the model's own month and years ahead", tips);
        check("a FOUND HERE row for each find: its name and how many years before the world", rows);

        boolean recipeRows = true;
        for (ResearchView.RecipeUse u : recipes) {
            String from = u.from() == null ? "known since the founding"
                    : u.from().source() == ResearchView.Source.HERE
                    ? String.format(java.util.Locale.ROOT, "found here in %s, %.1f years before the world", CityCalendar.format(u.from().month()), u.from().yearsAhead())
                    : "from abroad, " + CityCalendar.format(u.from().month());
            recipeRows &= has(sig, label(u.recipe().getName())) && has(sig, label(u.running() + " of " + u.buildings() + " building"
                    + (u.buildings() == 1 ? "" : "s"))) && has(sig, label(from));
        }
        check("a RECIPES IN USE row for each way: its name, how many of the building's stack run it, and where it came from", recipeRows);

        boolean pieceRows = true, words = true;
        for (ResearchView.PieceRow r : pieces) {
            String verdict = switch (r.verdict().kind()) {
                case FITTED -> "all fitted";
                case PAYS -> "the next fit pays";
                case NOT_YET -> "not worth fitting yet";
                case NOT_KNOWN -> "not known yet";
            };
            pieceRows &= has(sig, label(r.piece().getName())) && has(sig, label(r.fitted() + " of " + r.buildings() + " fitted"));
            words &= has(sig, label(verdict));
        }
        check("an EQUIPMENT FITTED row for each piece: its name, how many of its buildings carry it", pieceRows);
        check("...with the sector's own verdict in words (" + pieces.get(0).verdict().kind() + " on the first)", words);
        check("...each row a door to its sector's Investors page (a click, and the tooltip that says so)",
                has(sig, " click", "tip{" + pieces.get(0).piece().getName() + " on ", "Investors page."));
        // A signature line is its path (spaces, and "content: " or "graphic: " where it went through a scroller or a label) and then the node's class.
        long charts = sig.stream().filter(l -> l.replaceFirst("^[ a-z:]*", "").startsWith("TimeChart ")).count();
        long timelines = sig.stream().filter(l -> l.replaceFirst("^[ a-z:]*", "").startsWith("Timeline ")).count();
        check("the timeline is one pane with four charts under it (a researchers, a money, a found-against-abroad and a spread chart): "
                + timelines + " timeline, " + charts + " charts", timelines == 1 && charts == 4);

        // Where layout put the dots: all inside the pane, no two on each other.
        boolean inside = !dots.isEmpty() && dots.size() == timeline.size(), apart = true;
        for (double[] d : dots) inside &= d[0] >= 0 && d[1] >= 0 && d[0] + d[2] <= d[4] + .5 && d[1] + d[3] <= d[5] + .5;
        for (int i = 0; i < dots.size(); i++) {
            for (int j = i + 1; j < dots.size(); j++) {
                double[] a = dots.get(i), b = dots.get(j);
                apart &= !(Math.abs(a[0] - b[0]) < a[2] - .5 && Math.abs(a[1] - b[1]) < a[3] - .5);
            }
        }
        check("laid out, there is a dot for each of the " + timeline.size() + " discoveries, each inside the timeline's pane (" + dots.size() + " dots)", inside);
        check("...and no two dots stand on each other", apart);

        // What reshapes the page: another page and back, and a month on either side.
        List<Choice> choices = new ArrayList<>();
        choices.add(choice("the hub", policyOn(null, null)));
        choices.add(policyPage("Research", "Found"));
        choices.add(policyPage("Taxes", "Everything"));
        choices.add(policyPage("Research", "Found"));
        pageReshaped(town, bench, found, 95, choices, KP6_HANDED_BACK);

        System.out.println("\n--- 95: the timeline's stacking, its year ticks and City History's flags, as pure helpers ---");
        final int first = 1, last = 1201;
        final double plot = 1200, slot = KeptBench.researchSlot();
        check("the slot is the dot's width and its gap (" + slot + " px), and a month stands where its share of the span puts it",
                slot > 0 && KeptBench.researchX(first, first, last, plot) == 0 && KeptBench.researchX(last, first, last, plot) == plot
                        && KeptBench.researchX(601, first, last, plot) == 600);
        check("two dots a pixel apart stack: the second stands one above the first",
                java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 101}, first, last, plot), new int[]{0, 1}));
        check("two dots far apart share the floor",
                java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 500}, first, last, plot), new int[]{0, 0}));
        int apartBy = (int) slot;
        check("exactly the slot apart they do not stack, one pixel less they do",
                java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 100 + apartBy}, first, last, plot), new int[]{0, 0})
                        && java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 100 + apartBy - 1}, first, last, plot), new int[]{0, 1}));
        check("a bunch of three climbs, and a dot with room again goes back to the floor",
                java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 101, 102, 400}, first, last, plot), new int[]{0, 1, 2, 0})
                        && java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 101, 140}, first, last, plot), new int[]{0, 1, 0}));
        check("dots of one month stack one on another, no level past the count",
                java.util.Arrays.equals(KeptBench.researchLevels(new int[]{50, 50, 50, 50}, first, last, plot), new int[]{0, 1, 2, 3}));
        check("a wider plot parts dots that a narrow one stacked, and a narrower one stacks dots that were apart",
                java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 101}, first, last, plot * 20), new int[]{0, 0})
                        && java.util.Arrays.equals(KeptBench.researchLevels(new int[]{100, 120}, first, last, plot / 2), new int[]{0, 1}));
        check("and the same row at the same width stacks the same way twice",
                java.util.Arrays.equals(KeptBench.researchLevels(new int[]{10, 11, 12, 30, 31}, first, last, plot),
                        KeptBench.researchLevels(new int[]{10, 11, 12, 30, 31}, first, last, plot)));

        int[] ticks = KeptBench.researchTicks(first, last, plot);
        boolean januaries = ticks.length > 1, spaced = true, within = true;
        for (int i = 0; i < ticks.length; i++) {
            januaries &= CityCalendar.monthOfYear(ticks[i]) == 1;
            within &= ticks[i] >= first && ticks[i] <= last;
            if (i > 0) spaced &= KeptBench.researchX(ticks[i], first, last, plot) - KeptBench.researchX(ticks[i - 1], first, last, plot)
                    >= KeptBench.researchTickRoom() - 1e-6;
        }
        check("the year ticks are Januaries inside the axis, at least " + KeptBench.researchTickRoom() + " px apart (" + ticks.length + " over 100 years)",
                januaries && within && spaced);
        check("a narrower plot has fewer ticks, a wider one more", KeptBench.researchTicks(first, last, plot / 4).length < ticks.length
                && KeptBench.researchTicks(first, last, plot * 4).length > ticks.length);

        check("a flag's head counts its decisions and its discoveries apart, singular and plural right",
                "1 decision".equals(KeptBench.flagCounts(1, 0)) && "2 decisions".equals(KeptBench.flagCounts(2, 0))
                        && "1 discovery".equals(KeptBench.flagCounts(0, 1)) && "3 discoveries".equals(KeptBench.flagCounts(0, 3))
                        && "2 decisions · 1 discovery".equals(KeptBench.flagCounts(2, 1))
                        && "1 decision · 2 discoveries".equals(KeptBench.flagCounts(1, 2)));
        String violet = KeptBench.flagColour(ResearchView.FLAG_KIND);
        boolean own = true;
        for (String kind : new String[]{DecisionLog.TAX, DecisionLog.PROMISE, DecisionLog.CENTRAL_BANK, DecisionLog.CURRENCY, DecisionLog.BORROWING,
                DecisionLog.BANK, DecisionLog.FUND, DecisionLog.CONSTRUCTION, DecisionLog.RESERVE, DecisionLog.ZONING}) {
            own &= !violet.equals(KeptBench.flagColour(kind));
        }
        check("a discovery's flag has the research violet (" + violet + "), which no kind of decision has", own && violet.equalsIgnoreCase(ham.citybuildersim.ui.Palette.DISCOVERY));

        DecisionLog.Entry decision = new DecisionLog.Entry(5, DecisionLog.TAX, "Profit tax to 15%");
        DecisionLog.Entry later = new DecisionLog.Entry(8, DecisionLog.ZONING, "Zoned Agriculture");
        DecisionLog.Entry find = new DecisionLog.Entry(5, ResearchView.FLAG_KIND, "Found here: Test, 1.0 years before the world");
        List<ChartModel.Flag> flags = List.of(new ChartModel.Flag(5, List.of(decision, find)), new ChartModel.Flag(6, List.of(find)),
                new ChartModel.Flag(8, List.of(later)));
        List<DecisionLog.Entry> listed = KeptBench.decisionsInView(flags, 1, 10);
        check("YOUR DECISIONS lists the decisions, newest first, and leaves the discoveries out",
                listed.size() == 2 && listed.get(0) == later && listed.get(1) == decision);
        check("...and only those in the window: 6 to 7 holds a discovery and no decision", KeptBench.decisionsInView(flags, 6, 7).isEmpty()
                && KeptBench.decisionsInView(flags, 5, 5).size() == 1);
        check("a flag counts its decisions alone (a month of one decision and a find counts one; a find alone, none)",
                KeptBench.decisionsIn(flags.get(0)) == 1 && KeptBench.decisionsIn(flags.get(1)) == 0 && KeptBench.decisionsIn(flags.get(2)) == 1);
        // (The months played since the page was read have brought discoveries: the timeline is read again.)
        int now = ResearchView.timeline(town).size();
        List<ChartModel.Flag> real = ChartModel.flags(town.getDecisions(), ResearchView.flagEntries(town));
        int carried = 0;
        for (ChartModel.Flag f : real) carried += f.count() - KeptBench.decisionsIn(f);
        check("the flags City History is handed carry each of the town's " + now + " discoveries once beside its decisions (" + carried + ")",
                carried == now);
        tookWith("the Research area's Found page (95)", t, c);
    }

    /** A rail screen the clock is held to in section 88: how it is called here, the name its show method gives the window, and the call that shows it. */
    private record RailScreen(String label, String name, java.util.function.Consumer<KeptBench> show) { }

    /** The months landed, each followed by the clock's redraw, on every screen in section 88. */
    static final int CLOCK_MONTHS = 3;

    /**
     * Section 88: the clock never rebuilds a rail screen. UserInterface.rebuiltInPlace counts the draws that rebuilt the screen on show in place - the
     * page emptied, the height held and the scroll settled, as every redraw was before the screens were kept. The fixture that causes it: a page drawn
     * the old way twice under one name moves the count by one, and the next screen shown, an arrival, by none. Then every tab of the rail (and the
     * Build tab's construction page, and the city's fund on Finances) is shown through its real show method, and CLOCK_MONTHS months land, each followed
     * by what the clock does (the screen's own redraw): the count has not moved, the page's top nodes are the same nodes, the screen on show is the
     * same screen, and the redraw ran over the screen's own nodes (it handed some back - without that the others would hold for a redraw that did nothing).
     */
    static void kp7Clock(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 88: the clock never rebuilds a rail screen ---");
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        int[] probe = fx(bench::rebuildProbe);
        check("a page drawn the old way twice under one name moves the in-place count by one (it moved " + probe[0] + ")", probe[0] == 1);
        check("...and the next real screen shown, an arrival, does not move it (it moved " + probe[1] + ")", probe[1] == 0);

        List<String> buildPages = KeptBench.buildPages();
        String cityPage = null, marketPage = null;
        for (String p : buildPages.subList(1, buildPages.size())) {
            if (KeptBench.buildIsCity(p)) {
                if (cityPage == null) cityPage = p;
            } else if (marketPage == null) marketPage = p;
        }
        final String city = cityPage, market = marketPage;
        List<RailScreen> rail = new ArrayList<>();
        rail.add(new RailScreen("Build, the Overview", "handleAllBuildingMenus", b -> {
            b.buildOpen(buildPages.get(0));
            b.showBuild();
        }));
        if (city != null) rail.add(new RailScreen("Build, " + city, "handleAllBuildingMenus", b -> {
            b.buildOpen(city);
            b.showBuild();
        }));
        if (market != null) rail.add(new RailScreen("Build, " + market, "handleAllBuildingMenus", b -> {
            b.buildOpen(market);
            b.showBuild();
        }));
        rail.add(new RailScreen("the construction page", "showConstruction", KeptBench::showConstruction));
        rail.add(new RailScreen("the land office", "showLandMenu", KeptBench::showLand));
        rail.add(new RailScreen("People", "showPopulationInfoMenu", KeptBench::showPeople));
        rail.add(new RailScreen("Household money", "showHouseholdMenu", KeptBench::showHousehold));
        rail.add(new RailScreen("Services", "showServicesStatsMenu", KeptBench::showServices));
        rail.add(new RailScreen("Infrastructure", "showInfrastructureMenu", KeptBench::showInfrastructure));
        rail.add(new RailScreen("Sectors", "showSectorMenu", KeptBench::showSectors));
        rail.add(new RailScreen("Government", "showGovernmentMenu", KeptBench::showGovernment));
        rail.add(new RailScreen("Finances", "showFinanceMenu", KeptBench::showFinances));
        rail.add(new RailScreen("Finances, the city's fund", "showFinanceMenu", b -> {
            b.financesOpen("The city's fund", "Portfolio");
            b.showFinances();
        }));
        rail.add(new RailScreen("the Bank", "showBankMenu", KeptBench::showBank));
        rail.add(new RailScreen("Trade & the World", "showForeignMenu", KeptBench::showTrade));
        rail.add(new RailScreen("Policy", "showPolicyMenu", KeptBench::showPolicy));
        rail.add(new RailScreen("City History", "showHistoryMenu", KeptBench::showHistory));
        rail.add(new RailScreen("Rankings", "showRankingsMenu", KeptBench::showRankings));

        for (RailScreen s : rail) {
            Object[] r = fx(() -> {
                s.show().accept(bench);
                List<javafx.scene.Node> top = bench.pageTop();
                String shown = bench.screenShowing();
                int before = bench.rebuiltInPlace();
                for (int m = 0; m < CLOCK_MONTHS; m++) {
                    month(g);
                    bench.clockRedraw();
                }
                List<javafx.scene.Node> after = bench.pageTop();
                boolean same = !top.isEmpty() && after.size() == top.size();
                for (int i = 0; same && i < top.size(); i++) same = after.get(i) == top.get(i);
                return new Object[]{bench.rebuiltInPlace() - before, same, top.size(), shown, bench.screenShowing(), bench.drawnOnShow()};
            });
            int moved = (int) r[0];
            int[] drawn = (int[]) r[5];
            System.out.printf("  %-28s rebuilt in place %d over %d months; %d top nodes; the last redraw made %s and handed back %s%n", s.label(), moved, CLOCK_MONTHS,
                    (int) r[2], drawn == null ? "?" : String.valueOf(drawn[0]), drawn == null ? "?" : String.valueOf(drawn[1]));
            check("the clock's redraw of " + s.label() + " rebuilt nothing in place (" + moved + " in " + CLOCK_MONTHS + " months)", moved == 0);
            check("...and left its page's top nodes the same nodes (" + r[2] + " of them)", (boolean) r[1]);
            check("...and the screen on show is still " + s.name() + " (it was " + r[3] + ", it is " + r[4] + ")", s.name().equals(r[3]) && s.name().equals(r[4]));
            check("...and the redraw ran over the screen's own nodes (" + (drawn == null ? "no pool" : drawn[1] + " handed back, " + drawn[0] + " made") + ")",
                    drawn != null && drawn[1] > 0);
        }
        // What the section opened is put back: Finances on its hub, Build on its Overview.
        fx(() -> {
            bench.financesOpen(null, null);
            bench.buildOpen(buildPages.get(0));
            return null;
        });
    }

    /* =====================================================================
       HOUSEHOLD MONEY AND THE RANKINGS TAB (KP7), sections 82 to 90: the last two screens on the rail kept - each page drawn kept from the
       screen's pool and built new from an empty one, kept, shown as a page built new shows it, kept right through what reshapes it - then the
       clock on every rail screen (88, above), the hover and the tooltips, and what the kept month costs.
       ===================================================================== */

    /**
     * The budget for the kept Household money month - its draw, then the pulse's CSS and layout - on the page as it opens and with the first family
     * cell's books open (the larger of the two being the figure), at month 3,400 to 4,000 of a played city (about where KeptCheck is when it comes to
     * this page), on the PC: twice the kept month's median, rounded up (the page as it opens 2.7 to 3.1 ms in five runs of the KP7 sections alone, 2.8
     * between them, the same month built new 15.9 to 17.7 ms; with a cell open 3.6 to 7.0 ms, 3.8 between them, built new 19.2 to 29.7).
     */
    static final double KEPT_HOUSEHOLD_MS = 8;

    /**
     * The budget for the kept Rankings month - its draw, then the pulse's CSS and layout - on the tab as it opens and with every measure unfolded
     * (Browse all: a line a measure; the larger of the two being the figure), at month 3,400 to 4,000 of a played city, on the PC: twice the kept
     * month's median, rounded up (the tab as it opens 2.6 to 3.0 ms in five runs of the KP7 sections alone, 2.8 between them, the same month built new
     * 9.0 to 10.9 ms; unfolded 3.1 to 3.5 ms, 3.3 between them, built new 16.8 to 20.3).
     */
    static final double KEPT_RANKINGS_MS = 7;

    /** Sections 82 to 84: Household money kept, shown as a page built new shows it, and kept right through what reshapes it. */
    static void kp7Household(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== Household money (KP7) ===");
        // The clock paused: a month stepped by hand counts up at any speed.
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        KeptBench.Page page = fx(bench::householdMoney);
        pageKept(g, bench, page, 82);
        arrive(bench, page);
        pageShows(g, bench, page, 83);

        // 84: the view, every cell of the grid opened (a family cell of each shape and tier with anybody in it, and every row below the rule),
        // the statement, the books stacked under the grid, and a cell open across a month (pageReshaped lands one after every choice).
        List<String> cells = fx(bench::householdCells);
        long below = cells.stream().filter(c -> c.startsWith("OUT:")).count();
        System.out.printf("  the grid has %d cells to open (%d family cells and %d rows below the rule), all of them opened here%n", cells.size(), cells.size() - below, below);
        check("the grid has cells to open, family cells and rows below the rule (" + cells.size() + " of them, " + below + " below the rule)", below > 0 && cells.size() > below);
        String first = cells.get(0);
        List<Choice> reshape = new ArrayList<>();
        for (String v : KeptBench.householdViews()) reshape.add(choice("the view " + v, b -> { b.householdView(v); return null; }));
        reshape.add(choice("the view One household again", b -> { b.householdView("One household"); return null; }));
        for (String key : cells) reshape.add(choice("the cell " + key + " opened", b -> { b.householdCell(key); return null; }));
        reshape.add(choice("no cell open", b -> { b.householdCell(""); return null; }));
        reshape.add(new Choice("the city's month as a statement opened", b -> { b.householdStatement(true); return null; }, true));
        reshape.add(choice("...and closed", b -> { b.householdStatement(false); return null; }));
        reshape.add(choice("the view All of them with the first cell open", b -> { b.householdView("All of them"); b.householdCell(first); return null; }));
        reshape.add(choice("the books stacked under the grid (the page 1000 wide)", b -> { b.householdColumnWidth(1000); return null; }));
        reshape.add(choice("...a cell of another shape opened under it", b -> { b.householdCell(cells.get(cells.size() / 2)); return null; }));
        reshape.add(choice("...and the books beside the grid again (the page full width)", b -> { b.householdColumnWidth(0); b.householdView("One household"); return null; }));
        reshape.add(choice("shown through showHousehold(), the cell open", b -> { b.householdCell(first); b.forgetHousehold(); b.showHousehold(); return null; }));
        reshape.add(choice("...closed again", b -> { b.householdCell(""); return null; }));
        pageReshaped(g, bench, page, 84, reshape);

        // The premise of the stacked choices: the books sit beside the grid at the full width and under it when the page is 1000 wide.
        Boolean[] sides = fx(() -> {
            bench.home(page);
            bench.householdCell(first);
            bench.drawKept(page);
            bench.passes();
            Boolean beside = bench.householdBeside();
            bench.householdColumnWidth(1000);
            bench.drawKept(page);
            bench.passes();
            Boolean stacked = bench.householdBeside();
            bench.householdColumnWidth(0);
            bench.home(page);
            return new Boolean[]{beside, stacked};
        });
        check("the open cell's books sit beside the grid at the full width and under it at 1000 wide (beside: " + sides[0] + ", at 1000: " + sides[1] + ")",
                Boolean.TRUE.equals(sides[0]) && Boolean.FALSE.equals(sides[1]));
        fx(() -> {
            bench.home(page);
            return null;
        });
    }

    /** Sections 85 to 87: the Rankings tab kept, shown as a page built new shows it, and kept right through what reshapes it - to the pixel, since its table is painted. */
    static void kp7Rankings(Game g, KeptBench bench) throws Exception {
        System.out.println("\n=== The Rankings tab (KP7) ===");
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        KeptBench.Page page = fx(bench::rankings);
        pageKept(g, bench, page, 85);
        arrive(bench, page);
        // (The pixels at three months are part of every page's section 86; the table is painted on canvases, which no signature holds.)
        pageShows(g, bench, page, 86);

        // 87: every choice on the tab, the pictures compared to the pixel after each. The saved cities come after: a slot written, a read waited for.
        boolean nations = fx(bench::rankingsHasNations);
        List<String> measures = KeptBench.rankingsMeasures();
        java.util.Set<String> shown = new java.util.LinkedHashSet<>();
        for (int i = 0; i < measures.size(); i += 5) shown.add(measures.get(i));
        shown.add(measures.get(measures.size() - 1));
        for (String k : measures) {
            if (!KeptBench.rankingsSourced(k)) {
                shown.add(k);
                break;
            }
        }
        System.out.printf("  the file has %d measures; %d of them are shown here (every fifth, the last and the first no real place is ranked on); Your world is %s%n",
                measures.size(), shown.size(), nations ? "a board of this city's" : "not a board of this city's (it has no nations of its own)");
        List<Choice> c = new ArrayList<>();
        for (String b : KeptBench.rankingsBoards()) {
            if (b.equals("YOUR_WORLD") && !nations) continue;
            c.add(choice("the board " + b, x -> { x.rankingsBoard(b); return null; }));
        }
        c.add(choice("the board REAL_2025 again", x -> { x.rankingsBoard("REAL_2025"); return null; }));
        for (String m : KeptBench.rankingsMoneys()) c.add(choice("the money " + m, x -> { x.rankingsMoney(m); return null; }));
        for (String p : KeptBench.rankingsPrices()) c.add(choice("the prices " + p, x -> { x.rankingsPrice(p); return null; }));
        for (String v : KeptBench.rankingsConverts()) c.add(choice("the conversion " + v, x -> { x.rankingsConvert(v); return null; }));
        for (String m : shown) c.add(choice("the measure " + m, x -> { x.rankingsMeasure(m); return null; }));
        c.add(choice("the measure gdp again", x -> { x.rankingsMeasure("gdp"); return null; }));
        for (String f : KeptBench.rankingsFilters()) c.add(choice("the filter " + f, x -> { x.rankingsFilter(f); return null; }));
        c.add(choice("the filter ALL again, Around you", x -> { x.rankingsFilter("ALL"); x.rankingsAround(true); return null; }));
        c.add(choice("Around you on NATIONS", x -> { x.rankingsFilter("NATIONS"); return null; }));
        c.add(choice("the full list again", x -> { x.rankingsFilter("ALL"); x.rankingsAround(false); return null; }));
        c.add(choice("Browse all opened", x -> { x.rankingsBrowseAll(true); return null; }));
        c.add(choice("a measure pinned (" + measures.get(measures.size() - 1) + ") with every measure open", x -> { x.rankingsPin(measures.get(measures.size() - 1), true); return null; }));
        c.add(choice("...and the first unpinned (gdp)", x -> { x.rankingsPin("gdp", false); return null; }));
        c.add(choice("...Browse all folded", x -> { x.rankingsBrowseAll(false); return null; }));
        for (String d : fx(bench::rankingsDrawers)) c.add(choice("the drawer of " + d, x -> { x.rankingsDrawer(d); return null; }));
        c.add(choice("the drawer closed", x -> { x.rankingsDrawer(null); return null; }));
        pageReshaped(g, bench, page, 87, c, HANDED_BACK, true);

        // The saved cities: the city saved into slot 10 of the harness's own folder, a read of the folder, the answer waited for (it comes back through the FX thread).
        boolean wrote = quietly(() -> fx(bench::rankingsSaveSlot10));
        check("the city is saved into slot 10 of the harness's scratch folder (the player's own folder is refused)", wrote);
        fx(() -> {
            bench.rankingsRead();
            return null;
        });
        long deadline = System.nanoTime() + 60_000_000_000L;
        boolean landed = fx(bench::rankingsReadLanded);
        while (!landed && System.nanoTime() < deadline) {
            Thread.sleep(200);
            landed = fx(bench::rankingsReadLanded);
        }
        int onBoard = fx(bench::rankingsSavedOnBoard);
        check("the read of the saved cities landed within a minute, with the saved city on the board (" + onBoard + " on it)", landed && onBoard >= 1);
        List<Choice> y = new ArrayList<>();
        y.add(choice("the filter YOUR_CITIES with the saved city on it, Now", x -> { x.rankingsFilter("YOUR_CITIES"); x.rankingsAtAge(false); return null; }));
        y.add(choice("Your age", x -> { x.rankingsAtAge(true); return null; }));
        y.add(choice("Now again", x -> { x.rankingsAtAge(false); return null; }));
        for (String p : KeptBench.rankingsPrices()) y.add(choice("the prices " + p + " on Your cities", x -> { x.rankingsPrice(p); return null; }));
        for (String m : KeptBench.rankingsMoneys()) y.add(choice("the money " + m + " on Your cities", x -> { x.rankingsMoney(m); return null; }));
        for (String d : fx(bench::rankingsDrawers)) y.add(choice("the drawer of " + d + " on Your cities", x -> { x.rankingsDrawer(d); return null; }));
        y.add(choice("Your age with a drawer open", x -> { x.rankingsAtAge(true); return null; }));
        y.add(choice("the drawer closed, Now, the filter ALL (the saved city among the rows)", x -> { x.rankingsDrawer(null); x.rankingsAtAge(false); x.rankingsFilter("ALL"); return null; }));
        y.add(choice("the filter YOUR_SIZE", x -> { x.rankingsFilter("YOUR_SIZE"); return null; }));
        pageReshaped(g, bench, page, 87, y, HANDED_BACK, true);
        fx(() -> {
            bench.home(page);
            return null;
        });
    }

    /** The tooltips on a page across a month's kept redraw (setup made first): {tooltips before, on a node that has one after, the same Tooltip, on a node kept without one}. */
    static int[] tipsAcrossMonth(Game g, KeptBench bench, KeptBench.Page page) throws Exception {
        return fx(() -> {
            bench.drawKept(page);
            bench.passes();
            java.util.Map<javafx.scene.Node, javafx.scene.control.Tooltip> before = bench.tooltips();
            month(g);
            bench.drawKept(page);
            bench.passes();
            java.util.Map<javafx.scene.Node, javafx.scene.control.Tooltip> after = bench.tooltips();
            java.util.Set<javafx.scene.Node> still = bench.nodesOnPage();
            int both = 0, same = 0, lost = 0;
            for (java.util.Map.Entry<javafx.scene.Node, javafx.scene.control.Tooltip> e : before.entrySet()) {
                javafx.scene.control.Tooltip now = after.get(e.getKey());
                if (now == null) {
                    // (A node kept on the page that has no tooltip now lost the one it had: Kept takes the tooltip off a kept node its draw does not tip.)
                    if (still.contains(e.getKey())) lost++;
                    continue;
                }
                both++;
                if (now == e.getValue()) same++;
            }
            return new int[]{before.size(), both, same, lost};
        });
    }

    /**
     * Section 89: the hover and the tooltips. On Household money a vitals cell that is a door and the crumb door; on the Rankings tab a measure line
     * and the table's rows: a pointer put over the node and the page drawn kept again leave the same node in the look its own mouse-entered gives it,
     * and the pointer gone, the look its mouse-exited gives it (the table, whose rows are painted, lights the row under a pointer that has not moved,
     * and none when it has gone). And the tooltips are kept: a node a month's redraw keeps keeps its own Tooltip (Kept.tip) on both pages, with a
     * cell open, with a place's drawer open and with every measure unfolded.
     */
    static void kp7Hover(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 89: the hover is kept (Household money, the Rankings tab) ---");
        java.util.Map<String, Boolean> found = new java.util.LinkedHashMap<>();
        Hand none = b -> null;
        KeptBench.Page money = fx(bench::householdMoney);
        hoverKP6(g, bench, found, "Household money", money, none, "limitCell", "door");
        KeptBench.Page rankings = fx(bench::rankings);
        String line = fx(() -> {
            bench.home(rankings);
            bench.drawKept(rankings);
            bench.passes();
            return bench.hoverRankingsLine(() -> bench.drawKept(rankings));
        });
        hoverLine(found, "the Rankings tab", "measure line", line);
        String rows = fx(() -> {
            bench.home(rankings);
            bench.drawKept(rankings);
            bench.passes();
            return bench.hoverRankingsRows(() -> bench.drawKept(rankings));
        });
        hoverLine(found, "the Rankings tab", "table row", rows);
        check("Household money: a limitCell was found on the page of this city, to put the pointer on", found.getOrDefault("Household money|limitCell", false));
        check("the Rankings tab: a measure line was found on the page of this city, to put the pointer on", found.getOrDefault("measure line", false));
        check("the Rankings tab: a table row was found on the page of this city, to put the pointer on", found.getOrDefault("table row", false));
    }

    /** Section 89's last part: the tooltips are kept on Household money and the Rankings tab. */
    static void kp7Tips(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 89: the tooltips are kept (Household money, the Rankings tab) ---");
        String cell = fx(bench::householdCells).get(0);
        String city = fx(bench::rankingsDrawers).get(0);
        KeptBench.Page money = fx(bench::householdMoney);
        record Where(String screen, String name, KeptBench.Page page) { }
        List<Where> where = new ArrayList<>(List.of(
                new Where("Household money", "Household money", money),
                new Where("Household money", "Household money with the cell " + cell + " open", fx(() -> bench.withHome(money, "with a cell open", () -> bench.householdCell(cell)))),
                new Where("Household money", "Household money, all of them", fx(() -> bench.withHome(money, "all of them", () -> bench.householdView("All of them"))))));
        KeptBench.Page rankings = fx(bench::rankings);
        where.add(new Where("the Rankings tab", "the Rankings tab", rankings));
        where.add(new Where("the Rankings tab", "the Rankings tab with the city's drawer open", fx(() -> bench.withHome(rankings, "with a drawer open", () -> bench.rankingsDrawer(city)))));
        where.add(new Where("the Rankings tab", "the Rankings tab with every measure open", fx(() -> bench.withHome(rankings, "with every measure open", () -> bench.rankingsBrowseAll(true)))));
        java.util.Map<String, int[]> perScreen = new java.util.LinkedHashMap<>();
        for (Where w : where) {
            fx(() -> {
                bench.home(w.page());
                return null;
            });
            int[] c = tipsAcrossMonth(g, bench, w.page());
            int[] sum = perScreen.computeIfAbsent(w.screen(), k -> new int[4]);
            for (int i = 0; i < 4; i++) sum[i] += c[i];
            System.out.printf("  %s: %d tooltips, %d of them on a node that has one after a month's redraw, %d the same Tooltip, %d on a node kept without one%n", w.name(), c[0], c[1], c[2], c[3]);
            check(w.name() + ": every tooltip a node had and still has is the same Tooltip after a month's kept redraw (" + c[2] + " of " + c[1] + "), and none is lost from a node the page keeps (" + c[3] + ")",
                    c[1] == c[2] && c[3] == 0);
        }
        for (java.util.Map.Entry<String, int[]> e : perScreen.entrySet()) {
            check("the pages of " + e.getKey() + " carry tooltips that a month leaves on their nodes, to be kept (" + e.getValue()[1] + ")", e.getValue()[1] > 0);
        }
    }

    /**
     * Section 90: what the kept month costs on Household money - as it opens and with the first family cell's books open - and on the Rankings tab - as
     * it opens and with every measure unfolded - against the same month built new.
     */
    static void kp7Cost(Game g, KeptBench bench) throws Exception {
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        String cell = fx(bench::householdCells).get(0);
        KeptBench.Page money = fx(bench::householdMoney);
        KeptBench.Page open = fx(() -> bench.withHome(money, "Household money with the cell " + cell + " open", () -> bench.householdCell(cell)));
        pageCost(g, bench, money, 90, KEPT_HOUSEHOLD_MS, "KEPT_HOUSEHOLD_MS");
        pageCost(g, bench, open, 90, KEPT_HOUSEHOLD_MS, "KEPT_HOUSEHOLD_MS");
        KeptBench.Page rankings = fx(bench::rankings);
        KeptBench.Page unfolded = fx(() -> bench.withHome(rankings, "the Rankings tab with every measure open", () -> bench.rankingsBrowseAll(true)));
        pageCost(g, bench, rankings, 90, KEPT_RANKINGS_MS, "KEPT_RANKINGS_MS");
        pageCost(g, bench, unfolded, 90, KEPT_RANKINGS_MS, "KEPT_RANKINGS_MS");
        fx(() -> {
            bench.home(money);
            bench.home(rankings);
            return null;
        });
    }

    /** Sections 82 to 90: Household money and the Rankings tab kept, and the clock never rebuilding a rail screen (88). */
    static void screensKP7(Game g, KeptBench bench) throws Exception {
        System.out.printf("%n=== Household money, the Rankings tab and the clock on every rail screen (KP7): a city at month %d ===%n", g.getMonth());
        long t = System.nanoTime();
        int c = checks;
        kp7Household(g, bench);
        t = tookWith("Household money (82 to 84)", t, c);
        c = checks;
        kp7Rankings(g, bench);
        t = tookWith("the Rankings tab (85 to 87)", t, c);
        c = checks;
        kp7Clock(g, bench);
        t = tookWith("the clock on every rail screen (88)", t, c);
        c = checks;
        kp7Hover(g, bench);
        kp7Tips(g, bench);
        t = tookWith("the hover and the tooltips (89)", t, c);
        c = checks;
        kp7Cost(g, bench);
        tookWith("the cost (90)", t, c);
    }

    /* ----- a slider greyed while held (91) ----- */

    /** What one greying of a held slider did, for greyedWhileHeld to judge. */
    static final class Greyed {
        String none, differs, dropped, reading;
        boolean heldPressed, heldGreyed, changingGreyed, sameBar, heldAfter;
        double value, want, back, city;
    }

    /**
     * Section 91 (KP2b): a slider greyed while the player holds it lets go. A disabled node hears no mouse event, so a slider greyed with the button down
     * never hears its release: its held flag (and a drag's valueChanging) would stay set, a kept draw never sets the value of a held slider, and the thumb
     * would stop following the city for good. The fixture that causes it, twice on the policy rate's dial: pressed, and pressed and dragged a step
     * (the value changing), then greyed by hand - it is not held; ungreyed, a draw with another value staged under it puts the thumb on that value (and the
     * reading with it), the page equal to a page built new, and the staged value dropped, the thumb is back at the city's.
     */
    static void greyedWhileHeld(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 91: a slider greyed while held lets go ---");
        KeptBench.Page p = fx(() -> bench.policy("Policy, Money's policy rate page", "Money", "The policy rate"));
        for (boolean dragged : new boolean[]{false, true}) {
            String how = dragged ? "pressed and dragged a step" : "pressed";
            Greyed r = fx(() -> {
                Greyed x = new Greyed();
                bench.home(p);
                bench.showPolicy();
                bench.passes();
                javafx.scene.control.Slider bar = bench.policySliders().isEmpty() ? null : bench.policySliders().get(0);
                if (bar == null) {
                    x.none = "the page has no slider";
                    return x;
                }
                double step = bar.getBlockIncrement();
                x.city = bar.getValue();
                double snapped = bar.getMin() + Math.round((bar.getValue() - bar.getMin()) / step) * step;
                x.want = snapped + 2 * step <= bar.getMax() + 1e-9 ? snapped + 2 * step : snapped - 2 * step;
                KeptBench.press(bar);
                x.heldPressed = KeptBench.held(bar);
                if (dragged) {
                    bar.setValueChanging(true);
                    bar.setValue(snapped + (x.want > snapped ? step : -step));
                }
                // Greyed with the button down: the release can no longer be heard.
                bar.setDisable(true);
                x.heldGreyed = KeptBench.held(bar);
                x.changingGreyed = bar.isValueChanging();
                bar.setDisable(false);
                // The city's value (what the page is asked to show) elsewhere: a draw puts the thumb on it.
                bench.policyStage("policy", x.want);
                bench.showPolicy();
                bench.passes();
                x.sameBar = bench.policySliders().get(0) == bar;
                x.value = bar.getValue();
                x.reading = KeptBench.readingOf(bar).getText();
                x.heldAfter = KeptBench.held(bar);
                x.differs = againstNew(bench, p);
                bench.policyDrop();
                bench.showPolicy();
                bench.passes();
                x.back = bar.getValue();
                x.dropped = againstNew(bench, p);
                return x;
            });
            if (r.none != null) {
                check("a slider " + how + " then greyed: " + r.none, false);
                continue;
            }
            check("a slider " + how + " is held", r.heldPressed);
            check("...greyed while held it lets go (held " + r.heldGreyed + (dragged ? ", value changing " + r.changingGreyed : "") + ")", !r.heldGreyed && !r.changingGreyed);
            check("...ungreyed, a draw with another value staged puts the thumb on it (" + r.value + ", asked for " + r.want + ")",
                    r.sameBar && !r.heldAfter && Math.abs(r.value - r.want) < 1e-9);
            check("...and the page is equal to a page built new", r.differs == null);
            if (r.differs != null) System.out.println("  " + r.differs);
            check("...the staged value dropped, the thumb is back at the city's (" + r.back + ", was " + r.city + ")", Math.abs(r.back - r.city) < 1e-9 && r.dropped == null);
            if (r.dropped != null) System.out.println("  " + r.dropped);
        }
    }

    /**
     * Section 91's door cards (KP2b): a door card's hand style is set once a draw, whole. It was set to the plain card's and then to the door's (built on
     * what getStyle() said), so every card asked the CSS twice a month. The fixture: on the Policy hub (the area cards) and on the Taxes page (a tax's
     * card), a draw with nothing changed hears no change of any door card's style; and the counter hears a style set by hand.
     */
    static void doorsStyledOnce(Game g, KeptBench bench) throws Exception {
        for (Object[] where : new Object[][]{{"the Policy hub's area cards", null, null}, {"the Taxes page's tax cards (Everything)", "Taxes", "Everything"}}) {
            String name = (String) where[0];
            KeptBench.Page p = fx(() -> bench.policy("Policy, " + name, (String) where[1], (String) where[2]));
            String[] first = {null};
            int[] n = fx(() -> {
                bench.home(p);
                bench.showPolicy();
                bench.passes();
                List<javafx.scene.layout.VBox> doors = bench.policyDoors();
                int[] heard = {0};
                javafx.beans.InvalidationListener hear = o -> {
                    if (heard[0]++ == 0) first[0] = whoSet();
                    ((javafx.beans.value.ObservableValue<?>) o).getValue();
                };
                for (javafx.scene.layout.VBox d : doors) d.styleProperty().addListener(hear);
                bench.showPolicy();
                bench.passes();
                boolean same = bench.policyDoors().equals(doors);
                int quiet = heard[0];
                if (!doors.isEmpty()) doors.get(0).setStyle(doors.get(0).getStyle() + " ");
                int control = heard[0] - quiet;
                for (javafx.scene.layout.VBox d : doors) d.styleProperty().removeListener(hear);
                bench.showPolicy();
                bench.passes();
                return new int[]{doors.size(), quiet, control, same ? 1 : 0};
            });
            check(name + ": the page has door cards (" + n[0] + ") and a draw hands back the same ones", n[0] > 0 && n[3] == 1);
            check("...a draw with nothing changed sets no door card's style (" + n[1] + " heard)", n[1] == 0);
            if (n[1] > 0) System.out.println("  the first change heard was set by: " + first[0]);
            check("...and the counter hears a style set by hand (" + n[2] + ")", n[2] == 1);
        }
    }

    /** A notice raised now, unread, in the inbox. */
    private static Notice add(Game g, String key, String title) {
        Notice n = new Notice(key, title, List.of("first line of " + key, "", "third line"), g.getMonth());
        g.getInbox().all().add(n);
        return n;
    }

    /* =====================================================================
       THE KEPT SCREENS' COST SWEEP (KP2b), section 92: a style, a text, a graphic, a padding or a width set again on a node a draw hands
       back is a change CSS and layout are asked to look at every draw, and every month, for nothing
       ===================================================================== */

    /**
     * The files of the screens KP2b's sweep did not reach, whose writes section 92 names and does not count: the sweep was the list of
     * screens Pieces, the statements, Sectors, Land, the Bank, the fund, Finances, Trade, Zoning, Policy, Household money and the Rankings tab
     * make (KP4 to KP7), and a write on a page from one of these is another batch's mend (KP2c). Take a file out of this set as it is mended.
     */
    static final java.util.Set<String> NOT_KP2B = java.util.Set.of("UserInterface.java", "InfrastructureScreen.java", "ServicesScreen.java");

    /** The writes from NOT_KP2B's files, over the pages of section 92 (printed at its end). */
    static int ignoredSets = 0;

    /**
     * A page home, a hand on it, drawn and laid out, drawn once more with a listener on every style, text, graphic, padding and width on
     * it (KeptBench.redrawSets), no month between: how many of them the second draw wrote, and which were the first. The conditions that
     * make it a test are the draw's own: the nodes it hands back are the first draw's, with the values the first set.
     */
    static void nothingSet(KeptBench bench, KeptBench.Page page, String name, Hand hand) throws Exception {
        KeptBench.Sets sets = fx(() -> {
            bench.home(page);
            if (hand != null) hand.on(bench);
            bench.drawKept(page);
            // ...a count-up the first draw started (a figure counted up from last month's) run to its end, as a second draw some seconds later
            // would find it; a draw in the middle of one writes its figure, and that is its work.
            bench.finishPageAnimations();
            bench.passes();
            KeptBench.Sets s = bench.redrawSets(page, NOT_KP2B);
            bench.passes();
            return s;
        });
        if (sets.count() > 0) {
            System.out.println("  " + name + ": " + sets.count() + " written, from:");
            for (String mover : sets.first()) System.out.println("      " + mover);
        }
        ignoredSets += sets.ignored();
        check("a second kept draw of " + name + " in the same month sets no style, text, graphic, padding or width (" + sets.count() + ")",
                sets.count() == 0);
    }

    static void nothingSet(KeptBench bench, KeptBench.Page page, String name) throws Exception {
        nothingSet(bench, page, name, null);
    }

    /**
     * The pieces of paper on the city's books; where there are none (the months the sections before this played let every maturity pass) one
     * sold through the model, as the Borrow page's own button sells it, the first of a Term, a Note and a Serial the auction takes.
     */
    static List<Debt> piecesOnTheBooks(Game g, KeptBench bench) throws Exception {
        List<Debt> pieces = fx(bench::financesPieces);
        double ask = fx(() -> Math.max(g.minimumIssueSize() * 3, g.getCash() / 40));
        for (String[] kind : new String[][]{{"Term", "10"}, {"Note", "12"}, {"Serial", "10"}}) {
            if (!pieces.isEmpty()) break;
            String said = fx(() -> quietly(() -> bench.financesSell(kind[0], Integer.parseInt(kind[1]), ask, false)));
            pieces = fx(bench::financesPieces);
            System.out.println("  the books held no paper; " + kind[0] + " " + kind[1] + " sold through the model: "
                    + (pieces.isEmpty() ? "nothing came of it (" + said.split("\n")[0] + ")" : pieceWords(pieces.get(0))));
        }
        return pieces;
    }

    /**
     * Section 92 lets one tab's pages go before it opens the next (KP2b): the tab forgotten, as another city opened leaves it, and the
     * pages built new released (KeptBench.release). The harness JVM's heap is nearly full by section 92 (about 1.95 GB of 2 at its start).
     */
    static void letGo(KeptBench bench, java.util.function.Consumer<KeptBench> forget) throws Exception {
        fx(() -> {
            forget.accept(bench);
            bench.release();
            return null;
        });
    }

    /** Section 92: the pages the sweep mended, each redrawn with nothing new in the city. */
    static void kp2bSets(Game g, KeptBench bench) throws Exception {
        System.out.printf("%n=== The kept screens' cost sweep (KP2b): a city at month %d ===%n", g.getMonth());
        System.out.println("\n--- 92: a redraw with nothing new sets nothing ---");
        fx(() -> {
            bench.clock(false, null);
            bench.speed(0);
            return null;
        });
        // The premise: a node written twice is counted as often as it is written. A style made to change under the listener is counted
        // once for each change, and one set to what it is counted none.
        int[] twice = fx(() -> {
            javafx.scene.control.Label l = new javafx.scene.control.Label("x");
            l.setStyle("-fx-text-fill: red;");
            int[] n = {0};
            javafx.beans.InvalidationListener on = o -> {
                n[0]++;
                l.getStyle();
            };
            l.styleProperty().addListener(on);
            l.setStyle("-fx-text-fill: red;");
            int same = n[0];
            String before = l.getStyle();
            l.setStyle(before + " -fx-cursor: hand;");
            l.setStyle(before);
            return new int[]{same, n[0]};
        });
        check("a style set to what it is moves no listener, and set to another and back moves it twice (" + twice[0] + ", " + twice[1] + ")",
                twice[0] == 0 && twice[1] == 2);

        // The Ports page's berth row going to none: no city here has a Ports page with a kind gone to none, so the row is drawn from a berth
        // made by hand, by the page's own code, kept with berths and then with none against one built new with none.
        List<List<String>> berths = fx(bench::berthRowGoesToNone);
        String berthsDiffer = firstDifference(berths.get(0), berths.get(1));
        if (berthsDiffer != null) System.out.println("  first difference: " + berthsDiffer);
        check("a berth kind that goes to none is drawn as a row built new with none, bounds and all", berthsDiffer == null);

        fx(() -> ensureWorks(g));
        SectorKinds kinds = fx(() -> sectorKinds(g));
        String plain = kinds.plain(), works = kinds.materials();
        check("the city has a plain business and a materials industry with works to draw (" + plain + ", " + works + ")", plain != null && works != null);
        if (plain != null && works != null) {
            g.getBusinessInvestment().holdSector(works);
            String ops = KeptBench.sectorPages().get(0);
            nothingSet(bench, fx(bench::sectors), "the Sectors list");
            for (String key : new String[]{plain, Sectors.RETAIL, Sectors.REAL_ESTATE, works, Sectors.FREIGHT}) {
                for (String page : KeptBench.sectorPages()) {
                    nothingSet(bench, fx(() -> bench.sectors("the page", key, page, false)), possessive(called(g, key)) + " " + page + " page");
                }
            }
            for (String page : new String[]{"Income", "Balance sheet", "Cash & debt"}) {
                nothingSet(bench, fx(() -> bench.sectors("the page", plain, page, true)), possessive(called(g, plain)) + " " + page + " page as a statement");
            }
            int note = fx(() -> bench.sectorsNoteOf(plain));
            check("the first note on " + possessive(called(g, plain)) + " income statement opens on lines this month (the fixture's premise)", note > 0);
            nothingSet(bench, fx(() -> bench.sectors("the page", plain, "Income", true)),
                    possessive(called(g, plain)) + " income statement with note " + note + " open",
                    b -> { b.sectorsNote(plain, "income", note, true); return null; });
            nothingSet(bench, fx(() -> bench.sectors("the page", plain, ops, false)), possessive(called(g, plain)) + " Operations page with every line open",
                    b -> { b.sectorsFold("every", plain, true); return null; });
        }
        System.out.println("  [the Sectors tab done]");

        letGo(bench, KeptBench::forgetSectors);
        nothingSet(bench, fx(bench::bank), "the Bank Overview");
        for (String page : KeptBench.bankPages()) {
            nothingSet(bench, fx(() -> bench.bank("the page", "pages", page, false)), "the Bank's " + page + " page");
        }
        for (String page : new String[]{"Profit", "Balance sheet"}) {
            nothingSet(bench, fx(() -> bench.bank("the page", "pages", page, true)), "the Bank's " + page + " page as a statement");
        }
        letGo(bench, KeptBench::forgetBank);
        nothingSet(bench, fx(bench::finances), "the Finances hub");
        for (String area : KeptBench.financeAreas()) {
            for (String page : KeptBench.financePages(area)) {
                nothingSet(bench, fx(() -> bench.finances("the page", area, page)), "Finances, " + area + ", " + page);
            }
        }
        letGo(bench, KeptBench::forgetFinances);
        nothingSet(bench, fx(bench::trade), "the Trade Overview");
        for (String page : KeptBench.tradePages()) {
            nothingSet(bench, fx(() -> bench.trade("the page", page)), "Trade, " + page);
        }
        letGo(bench, KeptBench::forgetTrade);
        nothingSet(bench, fx(bench::policy), "the Policy hub");
        for (String[] ap : KeptBench.policyPages()) {
            nothingSet(bench, fx(() -> bench.policy("the page", ap[0], ap[1])), "Policy, " + ap[0] + ", " + ap[1]);
        }
        nothingSet(bench, fx(() -> bench.policy("the page", "Zoning", KeptBench.policyZonePage(Sectors.RETAIL))), "Policy, Zoning, the shops' zone");
        letGo(bench, KeptBench::forgetPolicy);
        fx(() -> ensureTransit(g));
        nothingSet(bench, fx(bench::infrastructure), "Infrastructure, Roads");
        for (String page : new String[]{"Transit", "Freight", "Ports"}) {
            nothingSet(bench, fx(bench::infrastructure), "Infrastructure, " + page, b -> { b.infraOpen(page, "All"); return null; });
        }
        for (String[] ap : KeptBench.servicesPages()) {
            nothingSet(bench, fx(bench::services), "Services, " + ap[0] + ", " + ap[1], b -> { b.servicesOpen(ap[0], ap[1]); return null; });
        }
        // Pages with something picked or opened: a line of paper and Close it now, a security, a nation, the land office with its details.
        List<Debt> pieces = piecesOnTheBooks(g, bench);
        check("the city's books hold a piece of paper (the fixture's premise for a line's page and Close it now: " + pieces.size() + ")", !pieces.isEmpty());
        if (!pieces.isEmpty()) {
            Debt first = pieces.get(0);
            nothingSet(bench, fx(() -> bench.finances("the page", "The book", "Every piece")), "Finances, a line's page", b -> { b.financesLine(first); return null; });
            nothingSet(bench, fx(() -> bench.finances("the page", "The book", "Every piece")), "Finances, Close it now", b -> { b.financesClose(first); return null; });
        }
        List<String> securities = fx(bench::fundSecurities);
        check("the fund lists a security (the fixture's premise for a security's page: " + securities.size() + ")", !securities.isEmpty());
        if (!securities.isEmpty()) {
            nothingSet(bench, fx(() -> bench.finances("the page", "The city's fund", "Portfolio")), "the fund, a security's page",
                    b -> { b.fundOpen(securities.get(0), "Portfolio"); return null; });
        }
        List<Integer> nations = fx(() -> bench.tradePartners(1));
        check("the city trades with a nation (the fixture's premise for a nation's page: " + nations.size() + ")", !nations.isEmpty());
        if (!nations.isEmpty()) nothingSet(bench, fx(() -> bench.tradeNation("the page", nations.get(0))), "Trade, a nation's page");
        nothingSet(bench, fx(bench::land), "the land office", b -> { b.landDetails(true); return null; });
        // The window abroad shut, last: a default abroad, as the model marks it, stays on the city for what is left of the run.
        fx(() -> {
            g.getDebtManager().markForeignDefault();
            return null;
        });
        check("the window abroad is shut (the fixture's premise): " + g.getDebtManager().foreignWindowReason(), !g.getDebtManager().foreignWindowOpen());
        nothingSet(bench, fx(() -> bench.finances("the page", "Borrow", "Abroad")), "Finances, Borrow, Abroad, the window shut");
        kp2bHouseholdMoney(g, bench);
        letGo(bench, KeptBench::forgetHousehold);
        kp2bRankings(g, bench);
        System.out.printf("  [%d writes from the files NOT_KP2B names were left uncounted over the pages above]%n", ignoredSets);
    }

    /** Section 92 on Household money (KP7, taken into KP2b): the page as it opens, each view, every cell of the grid opened, the statement and the books stacked under the grid. */
    static void kp2bHouseholdMoney(Game g, KeptBench bench) throws Exception {
        KeptBench.Page money = fx(bench::householdMoney);
        nothingSet(bench, money, "Household money");
        for (String view : KeptBench.householdViews()) {
            nothingSet(bench, money, "Household money, the view " + view, b -> { b.householdView(view); return null; });
        }
        List<String> cells = fx(bench::householdCells);
        check("the grid has a cell to open (the fixture's premise for Household money's open cells: " + cells.size() + ")", !cells.isEmpty());
        // A cell of each kind, not all of them (section 84 opens every one and compares it to a page built new): the first, the middle and the last
        // family cell and the first and the last row below the rule. The books under every cell are one piece of code; opening all 54 cells here held
        // the kept pages of each in the JVM's 2 GB beside the rest of the suite's, and ran it out of heap.
        java.util.Set<String> opened = new java.util.LinkedHashSet<>();
        List<String> family = cells.stream().filter(c -> !c.startsWith("OUT:")).toList(), below = cells.stream().filter(c -> c.startsWith("OUT:")).toList();
        for (List<String> kind : List.of(family, below)) {
            if (kind.isEmpty()) continue;
            opened.add(kind.get(0));
            opened.add(kind.get(kind.size() / 2));
            opened.add(kind.get(kind.size() - 1));
        }
        for (String key : opened) {
            nothingSet(bench, money, "Household money, the cell " + key + " open", b -> { b.householdCell(key); return null; });
        }
        nothingSet(bench, money, "Household money, the city's month as a statement", b -> { b.householdStatement(true); return null; });
        if (!cells.isEmpty()) {
            nothingSet(bench, money, "Household money, the view All of them with the first cell open",
                    b -> { b.householdView("All of them"); b.householdCell(cells.get(0)); return null; });
            nothingSet(bench, money, "Household money, the books stacked under the grid (the page 1000 wide)",
                    b -> { b.householdCell(cells.get(cells.size() / 2)); b.householdColumnWidth(1000); return null; });
        }
        fx(() -> {
            bench.home(money);
            return null;
        });
    }

    /**
     * The city saved into slot 10 of the harness's own folder and read back, so that the Rankings tab has a saved city on its board: section 87 does
     * it, and does nothing here where it has run. How many saved cities the screen's snapshot puts on the board.
     */
    static int rankingsSavedCities(KeptBench bench) throws Exception {
        if (fx(bench::rankingsSavedOnBoard) < 1) {
            quietly(() -> fx(bench::rankingsSaveSlot10));
            fx(() -> {
                bench.rankingsRead();
                return null;
            });
            long deadline = System.nanoTime() + 60_000_000_000L;
            boolean landed = fx(bench::rankingsReadLanded);
            while (!landed && System.nanoTime() < deadline) {
                Thread.sleep(200);
                landed = fx(bench::rankingsReadLanded);
            }
        }
        return fx(bench::rankingsSavedOnBoard);
    }

    /**
     * Section 93: the Rankings table's picture (KP2b), the one node on the page that paints its rows. A kept draw writes its cursor none (the hand is
     * in its style, which Kept leaves alone; set by code it is reset when the picture is handed back and set again, a change at each draw); and the
     * band that lights the row under the pointer follows a pointer that a wheel leaves still: the page scrolls, JavaFX sends no mouse-moved, and the
     * row the pointer is over is another (the band was left on the old one, and a month's draw lit the old one again).
     */
    static void kp2bRowsPicture(Game g, KeptBench bench) throws Exception {
        System.out.println("\n--- 93: the Rankings table keeps its hand, and its band follows a pointer a wheel leaves still ---");
        KeptBench.Page rankings = fx(bench::rankings);
        String hand = fx(() -> {
            bench.home(rankings);
            bench.drawKept(rankings);
            bench.passes();
            return bench.rankingsRowsCursor(() -> bench.drawKept(rankings));
        });
        if (hand != null) System.out.println("  the hand: " + hand);
        check("the Rankings table's picture has the hand, and a kept draw of the page writes its cursor none", hand == null);
        String wheel = fx(() -> {
            bench.home(rankings);
            bench.drawKept(rankings);
            bench.passes();
            return bench.hoverRankingsScroll(() -> bench.drawKept(rankings));
        });
        if (wheel != null) System.out.println("  the wheel: " + wheel);
        check("the Rankings table: after a wheel scrolls the page under a still pointer, and after a month lands, the band is on the row the pointer is over now", wheel == null);
        fx(() -> {
            bench.home(rankings);
            return null;
        });
    }

    /**
     * Section 92 on the Rankings tab (KP7, taken into KP2b): the page as it opens, each board, money, prices and conversion, some measures (every
     * tenth, the last and the first no real place is ranked on), each filter, Around you, Browse all, a measure pinned, a place's drawer, and the
     * saved city's board (Your cities, Your age, its drawer).
     */
    static void kp2bRankings(Game g, KeptBench bench) throws Exception {
        int saved = rankingsSavedCities(bench);
        check("the Rankings tab has a saved city on its board (the fixture's premise for Your cities: " + saved + ")", saved >= 1);
        KeptBench.Page page = fx(bench::rankings);
        nothingSet(bench, page, "the Rankings tab");
        boolean nations = fx(bench::rankingsHasNations);
        for (String board : KeptBench.rankingsBoards()) {
            if (board.equals("YOUR_WORLD") && !nations) continue;
            nothingSet(bench, page, "the Rankings tab, the board " + board, b -> { b.rankingsBoard(board); return null; });
        }
        for (String money : KeptBench.rankingsMoneys()) {
            nothingSet(bench, page, "the Rankings tab, the money " + money, b -> { b.rankingsMoney(money); return null; });
        }
        for (String prices : KeptBench.rankingsPrices()) {
            nothingSet(bench, page, "the Rankings tab, the prices " + prices, b -> { b.rankingsPrice(prices); return null; });
        }
        for (String convert : KeptBench.rankingsConverts()) {
            nothingSet(bench, page, "the Rankings tab, the conversion " + convert, b -> { b.rankingsConvert(convert); return null; });
        }
        List<String> measures = KeptBench.rankingsMeasures();
        java.util.Set<String> shown = new java.util.LinkedHashSet<>();
        for (int i = 0; i < measures.size(); i += 10) shown.add(measures.get(i));
        shown.add(measures.get(measures.size() - 1));
        for (String k : measures) {
            if (!KeptBench.rankingsSourced(k)) {
                shown.add(k);
                break;
            }
        }
        for (String m : shown) {
            nothingSet(bench, page, "the Rankings tab, the measure " + m, b -> { b.rankingsMeasure(m); return null; });
        }
        for (String f : KeptBench.rankingsFilters()) {
            nothingSet(bench, page, "the Rankings tab, the filter " + f, b -> { b.rankingsFilter(f); return null; });
        }
        nothingSet(bench, page, "the Rankings tab, Around you", b -> { b.rankingsAround(true); return null; });
        nothingSet(bench, page, "the Rankings tab, Browse all opened", b -> { b.rankingsBrowseAll(true); return null; });
        String last = measures.get(measures.size() - 1);
        nothingSet(bench, page, "the Rankings tab, " + last + " pinned with Browse all opened", b -> { b.rankingsBrowseAll(true); b.rankingsPin(last, true); return null; });
        List<String> drawers = fx(bench::rankingsDrawers);
        for (String d : drawers) {
            nothingSet(bench, page, "the Rankings tab, the drawer of " + d, b -> { b.rankingsDrawer(d); return null; });
        }
        nothingSet(bench, page, "the Rankings tab, Your cities", b -> { b.rankingsFilter("YOUR_CITIES"); b.rankingsAtAge(false); return null; });
        nothingSet(bench, page, "the Rankings tab, Your cities at Your age", b -> { b.rankingsFilter("YOUR_CITIES"); b.rankingsAtAge(true); return null; });
        for (String d : drawers) {
            nothingSet(bench, page, "the Rankings tab, Your cities with the drawer of " + d, b -> { b.rankingsFilter("YOUR_CITIES"); b.rankingsDrawer(d); return null; });
        }
        fx(() -> {
            bench.home(page);
            return null;
        });
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== KeptCheck: the People page, the chrome, City History, Infrastructure, Services, the land office, Build, the construction page, the Sectors tab, the Bank, Finances, the city's fund, Government, Trade & the World and Policy kept between redraws, the clock never rebuilding a rail screen, and the research screens (study UP4, UP2, KP1, KP2, KP3, KP4, KP5, KP6, KP7, R5) ===");
        String down = startJavaFx();
        if (down != null) {
            System.out.println("skipped: the JavaFX toolkit cannot start here (" + down + "); the page needs it to be laid out");
            System.exit(0);
        }
        GameFiles files = GameFiles.scratch("keptcheck");
        Game g = fx(() -> LongPlaytest.probeCity(files, null));
        fx(() -> {
            LongPlaytest.probePlay(g, CITY_MONTHS);
            return null;
        });
        KeptBench bench = fx(() -> new KeptBench(g));
        System.out.printf("a city played to month %d: %,d people%n", g.getMonth(), g.getPopulationManager().getPopulation());

        System.out.println("\n--- 1 and 2: the kept page, month after month, against a page built new ---");
        // The first draw makes the page; it is laid out before the months start.
        int firstMade = fx(() -> {
            bench.drawKept();
            bench.passes();
            return bench.lastMade();
        });
        System.out.printf("  the first draw made %d nodes through Kept%n", firstMade);
        int sameTop = 0, quiet = 0, differ = 0, madeMost = 0;
        long handedBack = 0;
        String firstDiffer = null;
        List<Integer> shotAt = List.of(1, MONTHS / 2, MONTHS);
        int shotsSame = 0, shotsAlike = 0;
        for (int m = 1; m <= MONTHS; m++) {
            final int at = m;
            Object[] r = fx(() -> {
                Object topBefore = bench.top();
                month(g);
                bench.drawKept();
                bench.passes();
                boolean same = bench.top() == topBefore;
                int made = bench.lastMade(), back = bench.lastKept();
                List<String> kept = bench.signature();
                int[] keptPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.showFresh();
                List<String> fresh = bench.signature();
                int[] freshPx = shotAt.contains(at) ? bench.pixels() : null;
                bench.restoreKept();
                return new Object[]{same, made, back, firstDifference(kept, fresh), keptPx, freshPx};
            });
            if ((boolean) r[0]) sameTop++;
            int made = (int) r[1];
            if (made == 0) quiet++;
            madeMost = Math.max(madeMost, made);
            handedBack += (int) r[2];
            if (r[3] != null) {
                differ++;
                if (firstDiffer == null) firstDiffer = "month " + g.getMonth() + ", " + r[3];
            }
            if (r[4] != null) {
                int[] a = (int[]) r[4], b = (int[]) r[5];
                if (java.util.Arrays.equals(a, b)) shotsSame++;
                if (alike(a, b)) shotsAlike++;
                if (!java.util.Arrays.equals(a, b)) {
                    System.out.println("  pixels differ at month " + g.getMonth() + ": " + pixelDifference(a, b));
                    String to = System.getProperty("keptcheck.out");
                    if (to != null) {
                        png(a, java.nio.file.Path.of(to, "kept-m" + g.getMonth() + ".png"));
                        png(b, java.nio.file.Path.of(to, "new-m" + g.getMonth() + ".png"));
                    }
                }
            }
        }
        System.out.printf("  %d months: %d nodes handed back a month on average; months that made no node: %d; the most made in one: %d%n",
                MONTHS, handedBack / MONTHS, quiet, madeMost);
        check("each month's redraw keeps the page it drew (the same top node, " + sameTop + " of " + MONTHS + ")", sameTop == MONTHS);
        check("each month hands back nine in ten of the nodes the first draw made, or more (" + handedBack / MONTHS + " of "
                + firstMade + ")", handedBack / MONTHS * 10 >= firstMade * 9L);
        check("most months make no node at all (" + quiet + " of " + MONTHS + ")", quiet * 2 > MONTHS);
        if (firstDiffer != null) System.out.println("  first difference: " + firstDiffer);
        check("the kept page's signature equals the new page's at every month (" + differ + " differ)", differ == 0);

        System.out.println("\n--- 3: and its pixels ---");
        System.out.printf("  %d of %d months the very same pixels%n", shotsSame, shotAt.size());
        check("the kept page and the new page are the same picture at " + shotAt.size() + " months, to SHADE_LEVELS ("
                + shotsAlike + ")", shotsAlike == shotAt.size());

        // The fixture that causes a difference: one figure on the kept page made wrong by hand.
        Object[] spoilt = fx(() -> {
            bench.drawKept();
            bench.passes();
            String was = bench.spoil();
            bench.passes();
            List<String> kept = bench.signature();
            int[] keptPx = bench.pixels();
            bench.showFresh();
            List<String> fresh = bench.signature();
            int[] freshPx = bench.pixels();
            bench.restoreKept();
            bench.drawKept();
            bench.passes();
            List<String> mended = bench.signature();
            return new Object[]{was, firstDifference(kept, fresh), alike(keptPx, freshPx), firstDifference(mended, fresh)};
        });
        check("one figure made wrong on the kept page (\"" + spoilt[0] + "\") parts its signature from the new page's",
                spoilt[0] != null && spoilt[1] != null);
        check("...and its picture", !(boolean) spoilt[2]);
        check("...and the next kept draw sets it right again", spoilt[3] == null);

        System.out.println("\n--- 4: what reshapes the page ---");
        String[][] changes = {
                {"the households' matrix opened", "matrix", "open"},
                {"the skill ladder's table opened", "ladder", "open"},
                {"the month card's year view on", "year", "on"},
                {"the households' matrix closed", "matrix", "close"},
                {"the skill ladder's table closed", "ladder", "close"},
                {"the month card's year view off", "year", "off"},
        };
        for (String[] c : changes) {
            String d = fx(() -> {
                if (c[1].equals("year")) bench.yearView(c[2].equals("on"));
                else bench.details(c[1], c[2].equals("open"));
                month(g);
                bench.drawKept();
                bench.passes();
                List<String> kept = bench.signature();
                bench.showFresh();
                List<String> fresh = bench.signature();
                bench.restoreKept();
                // ...and a month later, the page drawn over the reshaped one.
                month(g);
                bench.drawKept();
                bench.passes();
                List<String> kept2 = bench.signature();
                bench.showFresh();
                List<String> fresh2 = bench.signature();
                bench.restoreKept();
                String a = firstDifference(kept, fresh), b = firstDifference(kept2, fresh2);
                return a != null ? a : b;
            });
            if (d != null) System.out.println("  " + c[0] + ": " + d);
            check(c[0] + ": the redraw and the next equal a page built new", d == null);
        }

        System.out.println("\n--- 5: what the kept month costs ---");
        double[] cost = fx(() -> {
            List<Double> kept = new ArrayList<>(), fresh = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                month(g);
                double draw = bench.drawKept();
                double[] p = bench.passes();
                kept.add(draw + p[0] + p[1]);
            }
            // The same month built new: a page made from nothing in the column, as the redraw was before UP4.
            for (int i = 0; i < 9; i++) {
                month(g);
                double[] f = bench.showFresh();
                fresh.add(f[0] + f[1] + f[2]);
                bench.restoreKept();
            }
            return new double[]{median(kept), median(fresh)};
        });
        System.out.printf("  the page's month (its draw, CSS and layout), median of 9: kept %.1f ms, built new %.1f ms%n", cost[0], cost[1]);
        boolean ok = Timing.within(cost[0], KEPT_MONTH_MS);
        String timed = "the kept month is within KEPT_MONTH_MS (" + KEPT_MONTH_MS + " ms)";
        System.out.printf("%-72s %s%n", timed, Timing.verdict(ok, timed));
        if (!ok) fails++;

        chrome(g, bench);
        panels(g, bench);
        history(g, bench);
        infrastructure(g, bench);
        services(g, bench);
        land(g, bench);
        controls(g, bench);
        hover(g, bench);
        build(g, bench);
        construction(g, bench);
        stopped(g, bench);
        buildControls(g, bench);
        buildHover(g, bench);
        sectors(g, bench);
        researchScreens(g, bench);
        money(g, bench);
        screensKP6(g, bench);
        screensKP7(g, bench);
        researchFound(g, bench);
        greyedWhileHeld(g, bench);
        doorsStyledOnce(g, bench);
        kp2bSets(g, bench);
        kp2bRowsPicture(g, bench);

        System.out.println("\n" + (fails == 0 ? "ALL OK" : fails + " FAILED"));
        Platform.exit();
        System.exit(fails);
    }
}
