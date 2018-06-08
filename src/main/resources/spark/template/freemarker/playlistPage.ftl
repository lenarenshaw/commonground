<#assign content>
<#include "navbar.ftl">

<script src="https://sdk.scdn.co/spotify-player.js"></script>
<script src="/js/playlistPage.js"></script>

<p hidden id='playlist-id'>${playlist.getId()?c}<p>



<div align='center' style='align-items:center; align-items:baseline'>
    <p style="font-size:36px">
        <strong>
        ${playlist.getName()}
        </strong>
        |
        <button style='margin-top:0; padding: 10px 0px; border-radius:10px' class="centerButton" onclick="window.location.href='/commonground/group/${playlist.getGroup().getLinkName()}?code=${playlist.getGroup().getCode()?c}'" }>
            ${playlist.getGroup().getName()}
        </button>
    <p>
    <button class='centerButton' id='savePlaylistButton'>
        <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">playlist_add_check</i>
        &nbsp;Save and play!&nbsp;
    </button >
    <button class='centerButton' id='refreshPlaylistButton'>
        <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">refresh</i>
        &nbsp;Refresh Playlist&nbsp;&nbsp;
    </button >
</div>



<div align='center'>
<div class='flex-playlist-container'>
  <div id="embedded" style="display: none; height: 100px;">
    <iframe id="myPlaylist" src=""  onload="this.width=screen.width*0.75 ;" height="100%;" frameborder="0" allowtransparency="true" allow="encrypted-media"></iframe>
  </div>
  <table class='playlist-table'>
      <tr>
        <th id="song-title-header">Title</th>
        <th id="song-artist-header">Artist</th>
      </tr>
    <#list playlist.getSongs() as song>
    <tr>
      <td id="song-title"> ${song.getName()} </td>
      <td id="song-artist"> ${song.getArtist().getName()} </td>
        <!-- <a class='songInPlaylist'>
            <strong>
                ${song.getName()}
            </strong>

            ${song.getArtist().getName()}
        </a> -->
    </tr>
    <#else>
        <p>No songs in Playlist :(</p>
    </#list>
  </table>
</div>
</div>
</#assign>
<#include "main.ftl">
