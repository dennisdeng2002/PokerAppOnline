var webSocket = new WebSocket("ws://localhost:8080/PokerClient")

//All webSocket.on* functions are called when their respective
//annotated methods in the PokerWebSocket are called
webSocket.onopen = function(){
    addMessage("Connected to PokerApp")
}

webSocket.onclose = function(){
    addMessage("new")
    addMessage("Disconnected from PokerApp")
}

//These are messages received from the server
webSocket.onmessage = function(message){
    addMessage(message.data)
}

webSocket.onerror = function(error){
    console.log("Error", error)
}

//Displays messages on messageArea
function addMessage(message){
    var currentMessages = document.getElementById("messageArea")
    //Anytime a message starts with new we clear the messageArea
    //Allows for multi-line messages from the server (all messages are appended otherwise)
    if(message == "new"){
        currentMessages.value = ""
    }
    else{
        currentMessages.value = currentMessages.value + message + "\n"
    }
}

//Function is called when enter is pressed, sends content of the textfield
function sendToServer(message){
    webSocket.send(message.value)
    //Reset input field
    document.getElementById("inputField").value = ""
}
