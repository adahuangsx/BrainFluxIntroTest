# BrainFluxIntroTest
#### The warm-up test of BrainFlux Project (assigned on 9/17/2019)

***

### Steps:

Run influxd.exe;

In Eclipse, run the **Demo1Application.java** as "Spring Boot App" (Tomcat shows the port is 8080 in the console);

Go to **localhost:8080/start** and click "LOAD"

The screen shows "Loaded!" and the result of the query 
> SELECT mean(NO2_GT) FROM AirParameters WHERE time>=1072886400000000000 AND time<=1104508799000000000

is the mean of NO2(GT) in 2004

***

### Problems unsolved due to the time limit:

1. The result of the query need parsing while I only print the Series' toString() on the webpage;
2. I didn't implement the button but used a hyperlink instead;
3. I haven't read the interactive data table framework yet.

#### I will keep working on these. (9/24/2019)

***

#### UPDATE (9/29/2019)
1. I updated the SQL Query:

>SELECT mean(CO_GT) FROM AirParameters WHERE time>=1072933200000000000 AND time<=1104555599000000000 GROUP BY *

by adding Date as a tag;

2. Implement the button and a plain table of mean CO & NO2 everyday in 2004 (airquality.html);
3. Create a AirQuery class to faciliate displaying the table;
4. Set portal 8080 as default, so I can go to "localhost/start" directly.

### Problems unsolved:
1. I am learning JavaScript currently, but I use the recommended framework **Thymeleaf** to display the table instead;
2. "GROUP BY DATE" doesn't work, so I use "GROUP BY *" instead. I don't know why... 

***
