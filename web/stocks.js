
var ws = new WebSocket("ws://localhost:8080/stocks")

ws.onopen = function(){
    appendLog("Connected to stock service! Press 'Start' to get stock info.")
}

ws.onclose = function(){
    appendLog("Disconnected to stock service!")
}

ws.onmessage = function(evt){
    appendLog(evt.data)
}

ws.onerror = function(err){
    console.log("ERROR!", err)
}

function appendLog(logText){
    var log = document.getElementById("log");
    log.value = log.value + logText + "\n";
}

function sendToServer(msg){
    ws.send(msg)
}
