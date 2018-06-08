<#assign content>
<script src="/js/newGroup.js"></script>



<#include "navbar.ftl">

<div class='flex-page-container'>


<h1 class='center-text'>Make a new group!</h1>
<ul class='flex-h-container'>
<ul class='flex-v-container' style="align-items:center; justify-content:space-around">

<!-- <ul class="flex-v-container" style="align-items:center"> -->
    <input type="text" id="nameinput" placeholder="Enter group name..." style="">
    <button class='centerButton' id='createButton' style='margin:0'>
        <i class="material-icons" style="font-size: 36px; vertical-align:middle">group_add</i>
        &nbsp;Create Group&nbsp;
    </button>

</ul>
</ul>
</div>
</div>

</#assign>
<#include "main.ftl">
