var webSocket = new WebSocket("ws://localhost:8080/PokerClient")

//All webSocket.on* functions are called when their respective
//annotated methods in the PokerWebSocket are called
webSocket.onopen = function(){
    addMessage("Connected to PokerApp")
}

webSocket.onclose = function(){
    addMessage("Disconnected from PokerApp")
}

webSocket.onmessage = function(message){
    addMessage(message.data)
}

webSocket.onerror = function(error){
    console.log("Error", error)
}

function addMessage(message){
    var currentMessages = document.getElementById("messageArea")
    currentMessages.value = currentMessages.value + message + "\n"
}

function sendToServer(message){
    webSocket.send(JSON.stringify(message))
    //Reset input field
    document.getElementById("inputField").value = "";
}
