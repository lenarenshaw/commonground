<#assign content>
  <script src="/js/makePlaylist.js"></script>
  <script src="/chosen/chosen.jquery.js"></script>
  <script src="/js/genreDropdown.js"></script>
  <#include "navbar.ftl">

    <div class="flex-page-container">
      <h1 class='center-text'>
          <i class="material-icons md-48" style="font-size: 48px; vertical-align:middle">playlist_add</i>
          &nbsp;
          Make a new playlist
      </h1>
      <div class="flex-h-container" style="align-items: flex-start">
        <ul class='flex-v-container' style="align-items:center; justify-content:space-around">

          <!-- <h1 class='center-text'>Make a new playlist</h1> -->
          <br>
          <input type="text" id="playlist-name" placeholder="Enter playlist's name...">
          <br>
          <input type="number" id="playlist-size" placeholder="Enter number of songs...">
          <br>
          <a class='guiText' id="known-text" style="font-size: 20px;"> Playlist familiarity: 50%</a>
          <div id="textbox">
            <p class="alignleft guiText" style="font-size: 16px;">Discover music</p>
            <p class="alignright guiText" style="font-size: 16px;">Sing along to music</p>
          </div>
          <input type="range" min="1" max="100" value="50" class="slider" id="known-slider" style="margin-bottom: 5%;">
          <br>
          <select id="select-id" data-placeholder="Enter genres..." class='chosen-select' multiple style="width: 100%;">
      <#list genres as genre>
        <option> ${genre} </option>
      </#list>
    </select>
          <br>
          <p class="alignleft guiText" style="font-size: 20px;">Unsure about genre? Try these events instead!</p>
          <div id="container">
            <button class="userButton" onclick="dance()" style="display:inline-block;font-size:15px;">Dance party</button>
            <button class="userButton" onclick="instrumental()" style="display:inline-block;font-size:15px;">Study session</button>
            <button class="userButton" onclick="oldies()" style="display:inline-block;font-size:15px;">Old school</button>
          </div>
          <div id="container">
            <button class="userButton" onclick="roadtrip()" style="display:inline-block;font-size:15px;">American road trip</button>
            <button class="userButton" onclick="romance()" style="display:inline-block;font-size:15px;">Evening romance</button>
            <button class="userButton" onclick="alt()" style="display:inline-block;font-size:15px;">Alternative chill</button>
          </div>
          <button class='centerButton' id='generate-playlist'>
              <i class="material-icons md-48" style="font-size: 30px; vertical-align:middle">playlist_add_check</i>
              &nbsp;Create Playlist&nbsp;
          </button>
        </ul>
      </div>
    </div>

    <script type="text/javascript">
      function dance() {
        const selected = [];
        $('#select-id').val(selected.concat("pop").concat("dance").concat("hip hop").concat("funk")).trigger('chosen:updated');
      }

      function instrumental() {
        const selected = [];
        $('#select-id').val(selected.concat("classical").concat("acoustic").concat("instrumental")).trigger('chosen:updated');
      }

      function oldies() {
        const selected = [];
        $('#select-id').val(selected.concat("oldies").concat("contemporary").concat("rock-and-roll").concat("doo-wop")).trigger('chosen:updated');
      }

      function roadtrip() {
        const selected = [];
        $('#select-id').val(selected.concat("pop").concat("singer-songwriter")
          .concat("urban").concat("contemporary").concat("road").concat("country")
          .concat("western").concat("nashville").concat("classic").concat("easy").concat("boy")).trigger('chosen:updated');
      }

      function romance() {
        const selected = [];
        $('#select-id').val(selected.concat("r&b").concat("jazz")
          .concat("blues")).trigger('chosen:updated');
      }

      function alt() {
        const selected = [];
        $('#select-id').val(selected.concat("alternative").concat("indie")
          .concat("adult").concat("contemporary").concat("alt-indie")).trigger('chosen:updated');
      }
    </script>



</#assign>
<#include "main.ftl">
