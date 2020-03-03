# commonground
Created by Lena Renshaw, Calder Hoover, Eric Kong, and Kei Kinoshita
© Brown University, 2018

## Overview
Ever hang out with friends and can't decide what music to listen to? No worries, commonground can make that decision for you. Using each users' Spotify data, commonground can, for a group of people, create a playlist that contains songs every member in your group is sure to love. Users can select genre, mood, time of day, playlist length, and percentage of new vs. known songs for a group, and commonground will find the overlap between users' musical interests and generate an appropriate playlist.  

## How does it work? 
![GitHub Logo](/images/logo.png)

## Download and run
For this project, make sure to have [Java (JDK 11+)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) as well as [Maven (V 3.6+)](https://maven.apache.org/install.html) installed. Then, clone this repository, and run the following:
```
mvn clean
mvn package
./run -gui
```
This will run the Graphical User Interface, which you can navigate to in a web browser. We've set up the port at https://localhost:4567/commonground. From here, you can log in with your Spotify, add friends to your groups, and start generating playlists! 

## Fun functionality
  1. Users can generate playlists just for themselves, which can be great if they are looking for new songs in a particular genre, mood, or just looking to explore. 
  2. Playlists can be saved to Spotify for future use.
  3. Playlists can be modified within Spotify to add or remove songs. 
  4. The output of commonground is always changing to keep up with the new music that you're listening to.

## Project Specs, Mockup, and Design 
https://drive.google.com/open?id=1Ns5gZFZPMxNkAybjyGo5FxXovt4qpmJX
This folder has a link to documents associated with our project. The design document and presentation contain specs.
https://www.figma.com/file/gjQhPRrMR21v2bzr4nS2NSgp/common-ground-mockup
This figma document has our mockup and low-resolution design.
