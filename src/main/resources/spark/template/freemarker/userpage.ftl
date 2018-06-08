<#assign content>
<#include "navbar.ftl">

<div class='flex-page-container' style='justify-content:flex-start'>

  <div class='flex-v-container' style='flex-shrink:0; justify-content:center; height:90%'>

    <h1 class='center-text'>
        <i class="material-icons md-48" style="font-size: 48px; vertical-align:middle">person</i>
        &nbsp;
        ${user.getDisplayName()}
    </h1>

    <div class='flex-v-container' style='justify-content:center'>

        <div class='flex-h-container'>
            <i class="material-icons md-48" style="font-size: 48px; vertical-align:middle">group</i>
            &nbsp;
            <#list user.getGroups() as group>
                <button class="userButton" onclick="window.location.href='/commonground/group/${group.getLinkName()}?code=${group.getCode()?c}'" }>${group.getName()}</button>
            <#else>
                <a class='guiText'> You have no groups </a>
            </#list>
        </div>

    </div>
  </div>
</div>

</#assign>
<#include "main.ftl">
