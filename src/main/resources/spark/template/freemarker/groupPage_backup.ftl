<#assign content>
<script src="/js/groupPage.js"></script>
<script src="/chosen/chosen.jquery.js"></script>
<script src="/js/genreDropdown.js"></script>
<#include "navbar.ftl">

<div class="flex-page-container">

<h1 class='center-text'>${group.getName()}</h1>
<p class="guiSubHeading"> Group Code: ${code} </p>

<div class='flex-h-container'>
  <a class="guiText"> members: </a>
  <#list group.getUsers() as user>
      <button class="userButton" style='margin-top:0' onclick="window.location.href='/commonground/user/${user.getName()}'">${user.getName()}</a>

  <#else>
      <a class="guiText"> DEBUG: no users </a>
  </#list>
</div>

<div class="flex-h-container" style="align-items: flex-start">


<div class="flexbox-v-container">

    <ul id='new-playlist-pre' class="flexbox-v-container">
        <button class='centerButton' id='new-playlist-show-button'>Make a new playlist</button>
    </ul>

    <ul id='new-playlist-container' class='flex-v-container' style="align-items:center; justify-content:space-around, justify-content:space; display:none">
    <h2 class='center-text'>Make a new playlist</h2>

    <!-- TODO: CONSIDER REMOVING GROUP INSTANCE VARIABLE IN SERVER AND USING GROUP ID IN URL INSTEAD -->
    <input type="text" id="playlist-name" placeholder="Enter playlist's name...">
    <input type="number" id="playlist-size" placeholder="Enter number of songs...">
    <a class='guiText' id="known-text"> Playlist Familiarity: 50%</a>

        <input type="range" min="1" max="100" value="50" class="slider" id="known-slider" style="margin-bottom: 5%;">
    <select data-placeholder="Select genres..." class='chosen-select' multiple style="width: 100%;">
      <#list genres as genre>
        <option> ${genre} </option>
      </#list>
    </select>

    <button class='centerButton' id='generate-playlist'>Generate Playlist</button>
    </ul>
</div>

  <div class='flex-v-container'>
    <h2 class='center-text'>Group Information</h2>
    <a class="guiText"> Group Code: ${code} </a>



    <div class='flex-v-container'>
      <button class='centerButton' id='visualize'>Visualize Interests</button>
      <button class='centerButton' id='past-playlists'>Past Playlists</button>
      Playlists:
      <#list group.getPlaylists() as playlist>
          <button class="playlistlink" onclick="window.location.href='/commonground/playlist/${playlist.getName()}?code=${playlist.getCode()?c}'">${playlist.getName()}</a>
      <#else>
          <a class="guiText"> No playlists </a>
      </#list>
    </div>
  </div>

</div>
</div>



</#assign>
<#include "main.ftl">
