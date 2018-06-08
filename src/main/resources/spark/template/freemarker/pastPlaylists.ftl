<#assign content>
<script src="/js/groupPage.js"></script>
<script src="/chosen/chosen.jquery.js"></script>
<script src="/js/genreDropdown.js"></script>
<#include "navbar.ftl">

<div class="flex-page-container" style="justify-content: flex-start; flex-shrink:0">

    <div class='flex-v-container' style="justify-content:center; flex-shrink:0; height:90%">

  <p style="font-size:36px; text-align: center;margin-bottom: 0;">
      Past playlists |
          <button style='margin-top:0; padding: 10px 0px; border-radius:10px' class="centerButton" onclick="window.location.href='/commonground/group/${group.getLinkName()}?code=${group.getCode()?c}'" }>
              ${group.getName()}
          </button>
      <p>

  <div class='flex-v-container' style='align-items:center; flex-shrink: 0; padding-bottom:50px'>
    <#list group.getPlaylists() as playlist>
        <button class="playlistlink" onclick="window.location.href='/commonground/playlist/${playlist.getLinkName()}?code=${playlist.getCode()?c}'">${playlist.getName()}</a>
    <#else>
        <a class="guiText"> No playlists </a>
    </#list>
  </div>

</div>
</div>

</#assign>
<#include "main.ftl">
