<#assign content>
<script src="/js/groupPage.js"></script>
<script src="/chosen/chosen.jquery.js"></script>
<script src="/js/genreDropdown.js"></script>
<#include "navbar.ftl">

<div class="flex-page-container" style='align-items:center; margin-bottom: 0; text-align:center; justify-content:flex-start'>

<div class='flex-v-container' style='flex-shrink: 0; align-items:center; justify-content:center; height:90%; margin-bottom:50px'>

<h1 class='center-text'>
    <i class="material-icons md-48" style="font-size: 48px; vertical-align:middle; float:left">group</i>
    &nbsp;${group.getName()}
</h1>
<h2 class='center-text'> Code to join group:&nbsp;<strong>${code}</strong> </h2>

<h2 class='center-text' style='margin:10px 10px'>Members:&nbsp;</h2>

<div class='flex-h-container' style='margin-bottom: 0px; flex-shrink: 0'>

  <#list group.getUsers() as user>
      <button class="userButton" style='font-size:16px' onclick="window.location.href='/commonground/user/${user.getName()}'">${user.getDisplayName()}</a>
  <#else>
      <a class="guiText"> DEBUG: no users </a>
  </#list>
</div>

<div class="flex-h-container" style="align-items: flex-start;">
  <!-- <div style='display: grid; grid-template-columns: 1fr 1fr'> -->
    <!-- <h2 class='center-text'>Group Information</h2> -->
  <div class='flex-v-container' style="align-items: flex-start; flex-shrink: 0">
    <button class='centerButton' onclick="window.location.href='/commonground/newplaylist'" id='new-playlist'>
        <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">playlist_add</i>
        &nbsp;Make a new playlist
    </button>
    <button class='centerButton' id='past-playlists'>
        <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">history</i>
        &nbsp;View past playlists
    </button>
    <button class='centerButton' id='copy-code'>
        <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">content_copy</i>
        &nbsp;Copy code to clipboard
    </button>
  </div>

  <!-- <div class='flex-v-container' style="padding-right: 0em;">
    <button class='centerButton' id='visualize'>
        <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">remove_red_eye</i>
        &nbsp;Visualize interests
    </button>

</div>

  </div> -->






</div>

</div>

<p hidden id='group-code'>${code}</p>

</#assign>
<#include "main.ftl">
