<#assign content>

<script src="js/spotifyLogin.js"></script>

<div class='flex-page-container' style='text-align:center'>
    <h1 class='center-text'><span style="background-color:black; border-radius: 12.5px;">Welcome to CommonGround.</span></h1>
  <ul class='flex-h-container'>
    <ul class='flex-v-container'>
      <button id='login' class='centerButton' style="font-size:24px" align="center">Log in with Spotify</button>
    </ul>
  </ul>
</div>

<style>
body {
  background: url('img/background.gif');
  background-repeat: no-repeat;
    background-attachment: fixed;
    background-position: center;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
}
</style>


<!-- TODO: DELETE BELOW -->
<!--
<button id='tempButton2' class='centerButton' style="font-size:24px" align="center">Kei Test Button</button>



<a href="/commonground/login">Sync Spotify Account</a>
<button type="button" id="sync">Sync Spotify account2</button> -->


<!-- <a href="" id="loginbutton">LOGIN</a> -->



</#assign>
<#include "main.ftl">
