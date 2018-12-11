var ServerProxy = function () {
    this.handler = {
        'REPLICA': gMessageBroker.handleReplica,
        'POSSESS': gMessageBroker.handlePossess,
        'GAME_OVER': gMessageBroker.handleGameOver
    };
};

var opc = 0;

ServerProxy.prototype.setupMessaging = function() {
    var self = this;
    gInputEngine.subscribe('up', function () {
        self.socket.send(gMessageBroker.move('up'))
    });
    gInputEngine.subscribe('down', function () {
        self.socket.send(gMessageBroker.move('down'))
    });
    gInputEngine.subscribe('left', function () {
        self.socket.send(gMessageBroker.move('left'))
    });
    gInputEngine.subscribe('right', function () {
        self.socket.send(gMessageBroker.move('right'))
    });
    gInputEngine.subscribe('bomb', function () {
        self.socket.send(gMessageBroker.plantBomb());
    });
};

// Присоединяемся к серверу по сокету
// у WebSocketSession в Java имеется функция getURI() с помощью которого этот "Хвост" получается
ServerProxy.prototype.connectToGameServer = function(gameId) {
    console.log(gClusterSettings.gameServerUrl());
    //this.socket = new SockJS(gClusterSettings.gameServerUrl());
    this.socket = new SockJS("http://localhost:8090/connect");
    var self = this;
    var isStarted = false;
    this.socket.onmessage = function (event) {
        if (!isStarted) {
            isStarted = true;
            var bgAudio= document.getElementById('background');
            bgAudio.loop = true;
            bgAudio.play();
            console.log(bgAudio.canPlayType('audio/mp3'));
        }
        var msg = JSON.parse(event.data);
        if (msg.topic === 'DEAD') {
            $("#img2").css({"display": "block"});
            $("#img2").css({"opacity": "0"});
            bgAudio= document.getElementById('background');
            bgAudio.pause();
            bgAudio= document.getElementById('sad');
            bgAudio.play();
            setInterval(function(){chg_opc()}, 15);
            setTimeout(function(){my_reload();}, 3000);
        }
        if (msg.topic === 'WIN') {
            $("#img3").css({"display": "block"});
            $("#img3").css({"opacity": "0"});
            setInterval(function(){chg_opc2()}, 15);
            setTimeout(function(){my_reload();}, 3000);
        }
        var names = [];
        if (msg.topic === 'COUNT') {
            JSON.parse(msg.data, function(key,value) {
                console.log(key.toString());
                console.log("value:   " + value.toString());
                names.push(value.toString());
            });
            names.pop();
            document.getElementById("numbers").innerHTML = 'Current Players(' + names.length.toString() +
                '/' + nmb.toString() + '):';
                document.getElementById("curPlayers").innerHTML = '<ul>';
            for (var ind = 0; ind < names.length; ind++) {
                console.log(names[ind]);
                document.getElementById("curPlayers").innerHTML += '<li>' + names[ind].toString() + '</li>';
            }
            document.getElementById("curPlayers").innerHTML += '</ul>';
        }
        if (nmb == 4) {
            $("#numbers").css({"top": "-200px"});
            $("#curPlayers").css({"top": "-200px"});

        }
        if (self.handler[msg.topic] === undefined) {
            return;
        }
        self.handler[msg.topic](msg);
    };

    this.socket.onopen = function () {
        console.log("Ws connected");
        this.send(JSON.stringify({topic: "connect", name: name, gameId: gameId}));
    };

    this.socket.onclose = function (event) {
        console.log('Code: ' + event.code + ' cause: ' + event.reason);
    };

    this.socket.onerror = function (error) {
        console.log("Error " + error.message);
    };

    this.setupMessaging();
};

function chg_opc() {
    opc+= 0.01;
    if (opc > 1) {
        opc = 1;
    }
    $("#img2").css({"opacity": opc.toString()});
}

function chg_opc2() {
    opc += 0.01;
    if (opc > 1) {
        opc = 1;
    }
    $("#img3").css({"opacity": opc.toString()});
}

