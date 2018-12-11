var host = "localhost";
var port = 8080;
var LoginData = null;

var name = '';
var password = '';

function closeLoginForm() {
    $("#nameform").css({"display": "none"});
    $("#gagaga").css({"display": "none"});
    $("#name").val("");
}

function openGame() {
    document.body.style = "background-color: #ffffff;";
    document.getElementById('pisanina').innerText = name;
    $(".container").css({"display": "block"});
    $("#img1").css({"display": "block"});
}

my_reload = function() {
    document.cookie = 'name='+name;
    document.cookie = 'password='+password;
    window.location = window.location;
};

function login(a) {
    LoginData = $('#nameform').serialize();
    name = document.getElementById('name').value;
    document.cookie = 'name=';
    if (a === 1) {
        var settings = {
            "method": "POST",
            "crossDomain": true,
            "url": "http://" + host + ":" + port + "/matchmaker/login",
            "data": LoginData
        };
        $.ajax(settings).done(function (response) {
            closeLoginForm();
            openGame();
        }).fail(function (jqXHR) {
            LoginData = null;
            alert(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
            console.log(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
            name = "";
        });
    } else {
        var settings = {
            "method": "POST",
            "crossDomain": true,
            "url": "http://" + host + ":" + port + "/matchmaker/register",
            "data": LoginData
        };
        $.ajax(settings).done(function (response) {
            alert("Successfully registered");
            console.log("Successfully registered");
        }).fail(function (jqXHR, textStatus) {
            LoginData = null;
            alert(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
            console.log(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
            name = "";
        });
    }
}

logout = function() {
    LoginData = 'name='+name;
    var settings = {
        "method": "POST",
        "crossDomain": true,
        "url": "http://" + host + ":" + port + "/matchmaker/logout",
        "data": LoginData
    };
    $.ajax(settings).done(function (response) {
        console.log("Successfully logged out");
        document.cookie = 'name=';
        window.location = window.location;
    }).fail(function (jqXHR, textStatus) {
        LoginData = null;
        alert(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
        console.log(jqXHR.status + " " + jqXHR.statusText + ". " + jqXHR.responseText);
        name = "";
    });
}

trueload = function() {
    if (document.cookie.split('name=')[1].split(';')[0] != "") {
        name=document.cookie.split('name=')[1].split(';')[0];
    } else {
        return false;
    }
    closeLoginForm();
    openGame();
};

