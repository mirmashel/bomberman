var MatchMaker = function (clusterSetting) {
    this.settings = {
        url: clusterSetting.matchMakerUrl(),
        method: "POST",
        crossDomain: true,
        async: false
    };
};

MatchMaker.prototype.getSessionId = function (numb) {
    var namep = "name="+name;
    this.settings.data = namep+'&'+'players='+numb.toString();
    var sessionId = -1;
    $.ajax(this.settings).done(function(id) {
        sessionId = id;
    }).fail(function() {
        alert("Matchmaker request failed");
    });

    return sessionId;
};

gMatchMaker = new MatchMaker(gClusterSettings);