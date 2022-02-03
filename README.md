Web Crawler
===========

###Setup
To run the program you will need to set up a jsoup.jar dependencie.
To do so, right click Project Module (Softeq) -> Open Module Settings (or F4) ->
Dependencies -> `+` -> JARs or Directories -> select `jsoup-1.14.3.jar` from `Softeq/src/lib`.

After this, you should be able to compile and run the program. The main
function is in `src/main/WebCrawler`

###Results
The results are saved in two files, these are created automatically during
program execution. One is `results.csv`, where all site data is stored,
the second one `top10.csv` is created at program termination, where the 
results of top ten websites are stored.

###Performance
The program takes a while to run to cover all `10000` websites. For
testing purposes it is recommended to change this number to a more reasonable value 
reasonable,  say `200` or `500`. You can adjust this value in `src/main/WebCrawler`
by changing the `MAX_VISIT` variable.

####Program author
Benas Bulota  
2022