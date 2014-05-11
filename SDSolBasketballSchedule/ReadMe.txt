Andrew Prouty
CS 646 Android Mobile Application Development Spring Semester, 2014
Final Project, due 5/15/14 at 11:59 PM
Professor Roger Whitney

This is a mobile version of the game schedule for the SDSol Basketball club (http://www.sdsolbasketball.com/schedule.php).

http://sandiegosol.com/about/who-we-are/
SD Sol Approved of this project.
They are a very large regional club with hundred of teams which mostly play out of  facility on the AIU campus at scripts ranch.

http://www.leagueusa.com/ourcustomers.html
LeagueUSA is SD Sol's vendor who creates the scheduling application, AND also hosts the application.

LeagueUSA created the web services available here: http://www.sdsolbasketball.com/mobileschedule.php

The client-server requests include:
 -league
 -season
 -division
 -conference
 -team
 -game

There is currently only 1 league.
Smaller divisions only have 1 conference, so the user is not prompted to select it.

The one other UI feature is favorites.  Most people want access to only 1 or a few team schedules.  On any page they can use the menu to navigate directly to a team page.  On they team page they use the star button to add or remove the team from the favorites list.

CURRENT LIMITATIONS
-Everything is cached in Sqlite for fast & offline access. Only Division has a Toast warning if no information is available, others you get a blank screen if no content.
-If queried content was updated from current GET there is a toast message informing the user to exit/re-enter for updated results.
-no special screen for tablet

FUTURE
LeagueUSA has expressed interest in expanding this to many more leagues!  According to their website they have >5,000 leagues (little league, soccer, volleyball,...).
That could amount to millions of users.

The plan is for the initial league API call to be moved somewhere central.  The JSON response will then include a different URL for each league.
The application already has the plumbing to hold onto a different URL.
Before Android market to add:
-Toast message whenever no data in any mode
-Translate to Spanish
-League selection
-Start application in Season selection with the league choice from from the last session
-Add Back/Up to get from Season back to league
-Add refresh button to simplify refresh within the same screen.
-Rebrand from San Diego Sol to League USA

Discussion for after Android market
-import to Google calendar, team contact, location address for map

Not discussed yet
-download league specific logo to decorate their action bar
