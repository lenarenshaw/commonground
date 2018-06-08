$(document).ready(() => {
    const $newButton = $("#clickHere");
    $newButton.click(() => {
        // console.log($("groupname").val());
        window.location.href = '/commonground/welcome';
    });
});
