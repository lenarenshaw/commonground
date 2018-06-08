$(document).ready(() => {

    $("#past-playlists").click(() => {
      const str = document.URL;
      const n = str.lastIndexOf('/');
      const groupurl = str.substring(n + 1);
      window.location.href = '/commonground/pastplaylists/' + groupurl;
    });

    $("#copy-code").click(() => {
        console.log("click val: " + $("#group-code").text());
        copyToClipboard($("#group-code").text());
        $("#copy-code").html("<i class='material-icons md-48' style='font-size: 30px; vertical-align:middle'>check</i>&nbsp;Code copied!");
    });



    const copyToClipboard = str => {
        const el = document.createElement('textarea');  // Create a <textarea> element
        el.value = str;                                 // Set its value to the string that you want copied
        el.setAttribute('readonly', '');                // Make it readonly to be tamper-proof
        el.style.position = 'absolute';
        el.style.left = '-9999px';                      // Move outside the screen to make it invisible
        document.body.appendChild(el);                  // Append the <textarea> element to the HTML document
        const selected =
            document.getSelection().rangeCount > 0        // Check if there is any content selected previously
            ? document.getSelection().getRangeAt(0)     // Store selection if found
            : false;                                    // Mark as false to know no selection existed before
        el.select();                                    // Select the <textarea> content
        document.execCommand('copy');                   // Copy - only works as a result of a user action (e.g. click events)
        document.body.removeChild(el);                  // Remove the <textarea> element
        if (selected) {                                 // If a selection existed before copying
            document.getSelection().removeAllRanges();    // Unselect everything on the HTML document
            document.getSelection().addRange(selected);   // Restore the original selection
        }

    };

});
