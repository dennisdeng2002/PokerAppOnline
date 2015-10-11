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
    if (evt.substring(0,5) == "cards") {
        displayCards(evt);
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
        document.getElementById("opponentChips").innerHTML = '$'+amount.substring(6,amount.length);
    } else if (amount.charAt(5) == 'p') {
        document.getElementById("playerChips").innerHTML = '$'+amount.substring(6,amount.length);
    }
}

function displayCards(cards) {
    var colorOfFirstCard = determineCardColor(cards.charAt(7));
    var colorOfSecondCard = determineCardColor(cards.charAt(10));
    var symbolOfFirstSuit = determineSuitSymbol(cards.charAt(7));
    var symbolOfSecondSuit = determineSuitSymbol(cards.charAt(10));
    if (cards.charAt(5) == 'o') {
        document.getElementById("opponentCards").innerHTML = cards.substring(6,cards.length);
    } else if (cards.charAt(5) == 'p') {
        //document.getElementById("playerCards").innerHTML = cards.substring(6,cards.length);
        //lay out the text
        document.getElementById("playerFirstCardNum").innerHTML = cards.charAt(6);
        document.getElementById("playerFirstCardSuit").innerHTML = symbolOfFirstSuit;
        document.getElementById("playerSecondCardNum").innerHTML = cards.charAt(9);
        document.getElementById("playerSecondCardSuit").innerHTML = symbolOfSecondSuit;

        //style the color
        document.getElementById("playerFirstCardNum").style.color = colorOfFirstCard;
        document.getElementById("playerFirstCardSuit").style.color = colorOfFirstCard;
        document.getElementById("playerSecondCardNum").style.color = colorOfSecondCard;
        document.getElementById("playerSecondCardSuit").style.color = colorOfSecondCard;
    }
}

function determineSuitSymbol(suit) {
    switch(suit) {
        case 's':
            return "&#9824";
        case 'c':
            return "&#9827";
        case 'd':
            return "&#9830";
        case 'h':
            return "&#9829";
    }
}

function determineCardColor(suit) {
    switch(suit) {
        case 's': //black
            return "rgb(0,0,0)";
        case 'c': //green
            return "rgb(51, 214, 51)";
        case 'd': //blue
            return "rgb(0, 0, 205)";
        case 'h': //red
            return "rgb(232, 0, 0)";
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

