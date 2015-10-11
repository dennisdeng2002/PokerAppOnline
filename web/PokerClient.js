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
//received from java
//the conditionals inside this method look for special string formats
//produced by the server-side... if none of those "special cases" are met,
//the message will be treated normally and added to the message area.
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
    if (evt.substring(0,7) == "PREFLOP") {
        displayPreflop(evt);
    }
    if (evt.substring(0,4) == "FLOP") {
        displayFlop(evt);
        return;
    }
    if (evt.substring(0,4) == "TURN") {
        displayTurn(evt);
        return;
    }
    if (evt.substring(0,5) == "RIVER") {
        displayRiver(evt);
        return;
    }
    if (evt.substring(0,5) == "clear") {
        clearStreetConsole();
        return;
    }
    //else
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
    if (message == ""){ //if user enters a blank input disregard it
        return;
    }
    webSocket.send(message.value);
    //Reset input field
    document.getElementById("inputField").value = "";
}

//This function is called right before a new Hand is initiated. Clears out
//the display board of the street cards
function clearStreetConsole() {
    document.getElementById("street").innerHTML = "";
    document.getElementById("card1").innerHTML = "";
    document.getElementById("card2").innerHTML = "";
    document.getElementById("card3").innerHTML = "";
    document.getElementById("card4").innerHTML = "";
    document.getElementById("card5").innerHTML = "";
    //var consoleArea = document.getElementsByClassName("consoleArea").children;
    //for (i = 0; i < consoleArea.length; i++) {
    //    consoleArea[i].innerHTML = "";
    //}
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
    //serverside will never send an opponent's holecards
    if (cards.charAt(5) == 'o') {
        document.getElementById("opponentCards").innerHTML = cards.substring(6,cards.length);

        //but it will send our cards every time a hand is dealt
    } else if (cards.charAt(5) == 'p') {
        //lay out the text 1 by 1
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

//just prints PREFLOP to the street element
function displayPreflop(preflop) {
    document.getElementById("street").innerHTML = "PREFLOP";
}

//pre-condition: flop is formatted as: FLOP[5s, 4d, Kd]
function displayFlop(flop) {
    //get card numerals
    var card1 = flop.charAt(5);
    var card2 = flop.charAt(9);
    var card3 = flop.charAt(13);

    //get card colors
    var card1Color = determineCardColor(flop.charAt(6));
    var card2Color = determineCardColor(flop.charAt(10));
    var card3Color = determineCardColor(flop.charAt(14));

    //get card symbols
    var card1Symbol = determineSuitSymbol(flop.charAt(6));
    var card2Symbol = determineSuitSymbol(flop.charAt(10));
    var card3Symbol = determineSuitSymbol(flop.charAt(14));

    //display FLOP, card numeral, and card suit 1 by 1
    document.getElementById("street").innerHTML = "FLOP: ";
    document.getElementById("card1").innerHTML = card1 + card1Symbol;
    document.getElementById("card2").innerHTML = card2 + card2Symbol;
    document.getElementById("card3").innerHTML = card3 + card3Symbol;

    //assign the appropriate color
    document.getElementById("card1").style.color = card1Color;
    document.getElementById("card2").style.color = card2Color;
    document.getElementById("card3").style.color = card3Color;
}

//pre-condition: turn is formatted as: TURN[5s, 4d, Kd, Ac]
function displayTurn(turn) {
    var card4 = turn.charAt(17);
    var card4Color = determineCardColor(turn.charAt(18));
    var card4Symbol = determineSuitSymbol(turn.charAt(18));
    document.getElementById("street").innerHTML = "TURN: ";
    document.getElementById("card4").innerHTML = card4 + card4Symbol;
    document.getElementById("card4").style.color = card4Color;
}

//pre-condition: flop is formatted as: RIVER[5s, 4d, Kd, Ac, Jh]
function displayRiver(river) {
    //in order to make string indexing consistent with flop and turn (both 4 char long),
    //just splice the r in river to make it a 4 char string ("iver"). just makes it easier to follow
    var iver = river.substring(1,river.length);
    var card5 = iver.charAt(21);
    var card5Color = determineCardColor(iver.charAt(22));
    var card5Symbol = determineSuitSymbol(iver.charAt(22));
    document.getElementById("street").innerHTML = "RIVER: ";
    document.getElementById("card5").innerHTML = card5 + card5Symbol;
    document.getElementById("card5").style.color = card5Color;
}

//pre-condition: suit will be a char('s','c','d', or 'h').
//this method serves as an auxilliary helper method for replacing an 's' with
//an actual spade symbol for example...
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

//pre-condition: suit will be a char('s','c','d', or 'h').
//this method serves as an auxilliary helper method for determining what color
//suits are to be.
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

//pre-condition: name is formatted as: namepThisIsMyName or nameoThisIsYourName
//this function is called when both players are connected, displays the names
//in the respective positions
function displayNames(name) {
    if (name.charAt(4) == 'o') {
        document.getElementById("opponent").innerHTML = name.substring(5,name.length);
    } else if (name.charAt(4) == 'p') {
        document.getElementById("player").innerHTML = name.substring(5,name.length);

    }
}

//Don't really need this. We can just default this in HTML itself.
function setUserNames() {
    document.getElementById("inputField").placeholder = "Enter Input";
    document.getElementById("player").innerHTML = "YOU";
    document.getElementById("opponent").innerHTML = "OPPONENT";
}

