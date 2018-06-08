$(document).ready(() => {

    const $nameinput = $("#nameinput");

    $("#createButton").click(() => {
        const text = $("#nameinput").val();
        if (text.length > 0) {
          const postParameters = {groupname: text};
	        $.post("/addgroup", postParameters, responseJSON => {
	            const responseObject = JSON.parse(responseJSON);
	            const queryMap = {code: responseObject.code};
	            const queryParams = $.param(queryMap);
	            window.location.href = '/commonground/group/' + $nameinput.val() + "?" +queryParams;
	        });
        } else {
        	alert("Textbox is empty");
        }

    });

});
