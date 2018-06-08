$(document).ready(() => {

  // TODO: DELETE CONSOLE.LOGS

    const $newButton = $("#newGroupButton");
    $newButton.click(() => {
        console.log("clicked new group");
        // console.log($("groupname").val());
        window.location.href = '/commonground/newgroup';
    });

    const $profileButton = $("#myProfileButton");
    $profileButton.click(() => {
        console.log("clicked my profile");
        // console.log($("groupname").val());
        window.location.href = $profileButton.attr("href");
    });

    const $joinButton = $("#joinGroupButton");
    $joinButton.click(() => {
        console.log("clicked my profile");
        // console.log($("groupname").val());
        window.location.href = $joinButton.attr("href");
    });
});
