var ServerProxy = function () {
    this.handler = {
        'REPLICA': gMessageBroker.handleReplica,
        'POSSESS': gMessageBroker.handlePossess,
        'GAME_OVER': gMessageBroker.handleGameOver
    };
};

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
    gInputEngine.subscribe('jump', function () {
        self.socket.send(gMessageBroker.jump());
    });
};

// Присоединяемся к серверу по сокету
// у WebSocketSession в Java имеется функция getURI() с помощью которого этот "Хвост" получается
ServerProxy.prototype.connectToGameServer = function(gameId) {
    console.log(gClusterSettings.gameServerUrl());
    //this.socket = new SockJS(gClusterSettings.gameServerUrl());
    this.socket = new SockJS("http://localhost:8090/connect");
    console.log(this.socket);
    var self = this;
    this.socket.onmessage = function (event) {
        var msg = JSON.parse(event.data);
        //console.log(msg.data);
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
