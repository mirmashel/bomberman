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
    $(".container").css({"display": "block"});
}

my_reload = function() {
    document.cookie = 'name='+name;
    document.cookie = 'password='+password;
    window.location = window.location;
};

function login(a) {
    LoginData = $('#nameform').serialize();
    name = document.getElementById('name').value;
    password = document.getElementById('password').value;
    document.cookie = 'name=';
    document.cookie = 'password=';
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
            document.body.style = "background-color: #ffffff;";
            document.getElementById('pisanina').innerText = name
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

trueload = function() {
    if (document.cookie.split('name=')[1].split(';')[0] != "") {
        name=document.cookie.split('name=')[1].split(';')[0];
    } else {
        return false;
    }
    if (document.cookie.split('password=')[1].split(';')[0] != "") {
        password=document.cookie.split('password=')[1].split(';')[0];
    }
    document.getElementById('name').value = name;
    document.getElementById('password').value = password;
    login(1);
};

