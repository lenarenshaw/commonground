<#assign content>
<#include "navbar.ftl">

<script src="/js/joingroup.js"></script>


<div class='flex-page-container'>
    <div id='pre-join-div' class='flex-v-container' style='align-items:center'>

        <h1>Join a group!</h1>
        <br>
        <form>
            <input type="number" style="height: 35px;" id="groupcode" placeholder="Enter a group code...">
        </form>


        <button class='centerButton' style="padding: 15px; width: 250px;" id="submit">Join</button>
        <h2 class="center-text" id="message"></h2>
        <a class="center-text" style="font-size: 20px;"href="" id="link"></a>
    </div>


<div>
</#assign>
<#include "main.ftl">
