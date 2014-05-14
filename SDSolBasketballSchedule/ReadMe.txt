Andrew Prouty
CS 646 Android Mobile Application Development Spring Semester, 2014
Final Project, due 5/15/14 at 11:59 PM
Professor Roger Whitney

This is a mobile version of the game schedule for the SDSol Basketball club (http://www.sdsolbasketball.com/schedule.php).

http://sandiegosol.com/about/who-we-are/
SD Sol Approved of this project.
They are a very large regional club with hundred of teams and mostly play out of a large facility on the AIU campus in Scripps Ranch (San Diego).

http://www.leagueusa.com/ourcustomers.html
LeagueUSA is SD Sol's vendor who creates the scheduling application, builds AND hosts the web application that displays the game schedule.
They created the web services available here: http://www.sdsolbasketball.com/mobileschedule.php
They build JSON responses for:
1-leagues
2-seasons
3-divisions
4-conferences
4-teams
5-games

There currently returns only 1 league.
Most divisions only have 1 conference; the user is only prompted if there are multiple.

Beyond browsing the 1 significant UI feature is favorites to simplify navigation to frequented teams.
On any "team" page they use the star button to add or remove the team from the favorites list.
Once favorites exist a menu appears everywhere to allow for immediate navigation.  It opens a new window.

Everything is cached in Sqllite for off-line access and speed.
If cached/queried content exists then it will display, and then attempt fresher results from the web service.
If subsequently fetched on-line information differs the user will get the message: "Exit/return for updated results."

CURRENT LIMITATIONS
1-no refresh button to facilitate easier user-controlled refreshing
2-no separate/screen for tablet

FUTURE
LeagueUSA has expressed interest in expanding this to their other hosted leagues
According to their website they have >5,000 leagues (little league, soccer clubs...)!
The plan is for the initial league API to be moved somewhere central.  The JSON response will then include a different URL for each league.
The application already has the plumbing to store & utilize a base URL associated with the league.

Before Android market to add:
1-Translate to Spanish
2-League selection screen
3-Start application in Season selection with the league choice from from the last session
4-Add Back/Up to get from Season back to league
5-Re-name & update logo from San Diego Sol to League USA

Other possible ideas for after initially available:
1-Add refresh button to simplify refresh within the same screen
2-Download/display league specific logo to customize the action bar look to each league
3-Combine game with practice calendars (optional? SD Sol has in Google Calendars)
4-Team level option to connect (i.e. import) to Google calendar
5-Team level contact for email/phone
6-Game level ability to navigate to location for map/navigation.