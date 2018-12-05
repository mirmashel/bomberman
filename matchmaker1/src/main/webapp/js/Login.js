var host = "localhost";
var port = 8080;
var LoginData = null;

function closeLoginForm() {
    $("#nameform").css({"display": "none"});
    $("#name").val("");
}

function login(a) {
    LoginData = $('#nameform').serialize();
    if (a === 1) {
        var settings = {
            "method": "POST",
            "crossDomain": true,
            "url": "http://" + host + ":" + port + "/matchmaker/login",
            "data": LoginData
        };
        $.ajax(settings).done(function (response) {
            closeLoginForm();
        }).fail(function (jqXHR, textStatus) {
            LoginData = null;
            alert(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
            console.log(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
        });
    } else {
        var settings = {
            "method": "POST",
            "crossDomain": true,
            "url": "http://" + host + ":" + port + "/matchmaker/register",
            "data": LoginData
        };
        $.ajax(settings).done(function (response) {
        }).fail(function (jqXHR, textStatus) {
            LoginData = null;
            alert(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
            console.log(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
        });
    }
}

