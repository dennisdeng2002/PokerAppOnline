var webSocket = new WebSocket("ws://localhost:8080/PokerClient")

//All webSocket.on* functions are called when their respective
//annotated methods in the PokerWebSocket are called
webSocket.onopen = function(){
    addMessage("Connected to PokerApp");
    setUserNames();
}

webSocket.onclose = function(){
    addMessage("new");
    addMessage("Disconnected from PokerApp");
}

//These are messages received from the server
//received from java(?)
webSocket.onmessage = function(message){
    var evt = message.data;
    if (evt.substring(0,5) == "chips") {
        displayChips(evt);
        return;
    }
    if (evt.substring(0,4) == "name") {
        displayNames(evt);
        return;
    }
    addMessage(evt);
}

webSocket.onerror = function(error){
    console.log("Error", error);
}

//Displays messages on messageArea
function addMessage(message){
    var currentMessages = document.getElementById("messageArea");
    //Anytime a message starts with new we clear the messageArea
    //Allows for multi-line messages from the server (all messages are appended otherwise)
    if(message == "new"){
        currentMessages.value = "";
    }
    else{
        currentMessages.value = currentMessages.value + message + "\n";
    }
}

//Function is called when ENTER is pressed, sends content of the textfield
//message gets sent to Java(?)
function sendToServer(message){
    if (message == ""){
        return;
    }
    webSocket.send(message.value);
    //Reset input field
    document.getElementById("inputField").value = "";
}

//o for opponent and p for player
function displayChips(amount) {
    if (amount.charAt(5) == 'o') {
        document.getElementById("opponentChips").innerHTML = amount.substring(6,amount.length);
    } else if (amount.charAt(5) == 'p') {
        document.getElementById("playerChips").innerHTML = amount.substring(6,amount.length);
    }
}

function displayNames(name) {
    if (name.charAt(4) == 'o') {
        document.getElementById("opponent").innerHTML = name.substring(5,name.length);
    } else if (name.charAt(4) == 'p') {
        document.getElementById("player").innerHTML = name.substring(5,name.length);
    }
}

function setUserNames() {
    document.getElementById("inputField").placeholder = "Enter Input";
    document.getElementById("player").innerHTML = "YOU";
    document.getElementById("opponent").innerHTML = "OPPONENT";
}

