<#assign content>

<script src="/js/welcomeScreen.js"></script>
<script src="/js/refreshSongs.js"></script>

<#include "navbar.ftl">


<div class='flex-page-container' style="justify-content:flex-start">



<div class='flex-v-container' style='height:90%; flex-shrink: 0; justify-content:center'>
<h1 class='center-text'>Welcome, ${displayName}</h1>

<ul class='flex-h-container' style='flex-shrink: 0;'>


<ul class='flex-v-container' style="flex-shrink: 0; justify-content:flex-start">

<button class='centerButton' href="/commonground/newgroup" id='newGroupButton' style="font-size:24px">
    <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">group_add</i>
    &nbsp;Create a new group
</button>
<button class='centerButton' href="/commonground/user/${userName}" id='myProfileButton' style="font-size:24px">
    <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">person</i>
    &nbsp;My groups
</button>
<button class='centerButton' href="/commonground/joingroup" id='joinGroupButton' style="font-size:24px">
    <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">group</i>
    &nbsp;Join a group
</button>
<button class='centerButton' id='refreshSongs'>
    <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">refresh</i>
    &nbsp;New music? Refresh!
</button>

</ul>
</ul>


</div>
</div>

</#assign>
<#include "main.ftl">
