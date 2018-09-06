# CommonSound: cs0320 Term Project 2018
Created by Lena Renshaw, Calder Hoover, Eric Kong, and Kei Kinoshita

**Project Idea:**

Our project aims to quickly build playlists that conform to the music taste of many people. Playlists take time to make, and you can never be sure if the majority of people will like the final result. We take the spotify data of a group of people and aim to intelligently create a playlist such that with high probability a majority of the group will enjoy the music. 

Q: What are the critical features we need to develop? For each feature, why is it being included and what do we expect to be most challenging about developing this feature?

1. Group selection/getting data from each member: select people in the group you want to create the playlist for, and collect their spotify music data. 

    1. This is being included because it is necessary to collect data before performing any analysis/algorithms. 

    2. It will be challenging to organize the data in a useful way in a database, especially given our group is new to databases. It will also be challenging to utilize the Spotify API to collect data efficiently. A third challenge would be to come up with the appropriate data structure(s) to store the data so that not too much memory is used and querying can be fast. 

2. Generating playlists based on collected data.
    1. This is being included because it is the core component of our project - creating a playlist that everyone will enjoy.
    2. The most challenging part of this feature will be coming up with an algorithm that weighs everyone’s preferences equally and effectively chooses songs based on all of the data we have (songs in playlists, number of times played, overlap between friends, etc). This might take a lot of trials to get correctly and possibly some user testing. 

3. (Optional extension) Visualization of friends’ music interests:
    1. This is an optional feature because it might be nice for the user to see how the playlist is being generated via a visualization of some sort. However, it might be too much work on top of automatic playlist generation.
    2. The most challenging part of this feature would be organizing the data in an efficient way. This would probably require some creative databases work, akin to that required for data collection.

4. (Optional extension) Suggesting friends with similar music tastes
    1. This is an optional feature that we think would be fun to include because it goes along nicely with this project, but it isn’t the focus of our project so it’s not one of our minimum requirements. It would parse through either your spotify friends or your facebook friends with spotify and find people who have similar music tastes as you so you could become friends/share music/go to concerts together.
    2. The two most challenging parts about this feature would be going through all of the friends and collecting data on them and developing some sort of algorithm to determine if a friend is a good match (i.e., maybe you don’t share the same songs but you like the same genres, so that would be a good match, etc).

## Project Specs, Mockup, and Design 
https://drive.google.com/open?id=1Ns5gZFZPMxNkAybjyGo5FxXovt4qpmJX
This folder has a link to documents associated with our project. The design document and presentation contain specs.
https://www.figma.com/file/gjQhPRrMR21v2bzr4nS2NSgp/common-ground-mockup
This figma document has our mockup and low-resolution design, with high resolution images coming soon.
