$(document).ready(() => {
	const $groupcode = $("#groupcode");
	const $submit = $("#submit");
    $submit.click(event => {
    	console.log("AAA");
    	let text = $groupcode.val();
    	const postParameters = {groupcode: text};
        $.post("/joingroup", postParameters, responseJSON => {
            const responseObject = JSON.parse(responseJSON);
            if (responseObject.message.split(":")[0] === "ERROR"){
            	alert(responseObject.message);
            }
            $("#message").text(responseObject.message);
            $("#link").attr("href", responseObject.link);
            $("#link").text(responseObject.link);
        }); 
        // 13 is the key code for the Enter key
        console.log("hi");
        //$.get("/commonground/login")
        
        
    });
});
