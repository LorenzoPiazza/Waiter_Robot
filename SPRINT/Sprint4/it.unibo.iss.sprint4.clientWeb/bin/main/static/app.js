var stompClient = null;
var hostAddr = "http://localhost:7001/ring";

//SIMULA UNA FORM che invia comandi POST
function sendRequestData( params, method) {
    method = method || "post"; // il metodo POST � usato di default
    //console.log(" sendRequestData  params=" + params + " method=" + method);
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", hostAddr);
    var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "temp");
        hiddenField.setAttribute("value", params);
     	//console.log(" sendRequestData " + hiddenField.getAttribute("name") + " " + hiddenField.getAttribute("value"));
        form.appendChild(hiddenField);
    document.body.appendChild(form);
    console.log("body children num= "+document.body.children.length );
    form.submit();
    document.body.removeChild(form);
    console.log("body children num= "+document.body.children.length );
}


function postJQuery(themove){
var form = new FormData();
form.append("name",  "move");
form.append("value", "r");

let myForm = document.getElementById('myForm');
let formData = new FormData(myForm);


var settings = {
  "url": "http://localhost:8080/move",
  "method": "POST",
  "timeout": 0,
  "headers": {
       "Content-Type": "text/plain"
   },
  "processData": false,
  "mimeType": "multipart/form-data",
  "contentType": false,
  "data": form
};

$.ajax(settings).done(function (response) {
  //console.log(response);  //The web page
  console.log("done move:" + themove );
});

}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/it-unibo-iss');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/topic/displayWaiterLogic', function (msg) {
            showMsgWaiterLogic(JSON.parse(msg.body).content);
       });
        stompClient.subscribe('/topic/displaySmartbell', function (msg) {
             showMsgSmartbell(JSON.parse(msg.body).content);
        });
        stompClient.subscribe('/topic/displayClient1', function (msg) {
            showMsgClient1(JSON.parse(msg.body).content);
       });
        stompClient.subscribe('/topic/displayClient2', function (msg) {
            showMsgClient2(JSON.parse(msg.body).content);
       });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}


function sendRing(bodyTemp){
	console.log("send ring(" + bodyTemp + ")");
	$("#ringOk"	 ).attr("disabled", true);
	$("#ringSick").attr("disabled", true);
    stompClient.send("/app/ring", {}, JSON.stringify({'name': bodyTemp }))
}

function sendProceed(clientN){
	console.log("send proceed for client (" + clientN + ")");
	if(clientN == "1"){
		$("#proceed1").attr("disabled", true);
		stompClient.send("/app/proceed1", {}, JSON.stringify({'name': clientN }))
	}
	if(clientN == "2"){
		$("#proceed2").attr("disabled", true);
		stompClient.send("/app/proceed2", {}, JSON.stringify({'name': clientN }))
	}
}

/*function sendUpdateRequest(){
	console.log(" sendUpdateRequest "  );
    stompClient.send("/app/update", {}, JSON.stringify({'name': 'update' }));
}*/

function showMsgSmartbell(message) {
	console.log(message);
	if(message.startsWith("smartbell")){	//Defensive programming...
		if(message === "smartbell | listening to ringing..."){
			$("#ringOk").attr("disabled", false);
			$("#ringSick").attr("disabled", false);
		}
		if( $("#applmsgs").text() != "" ){
			$("#previousapplmsg").prepend("<tr><td>" + $("#applmsgs").text() + "</td></tr>");
		}
		$("#applmsgs").html( "<pre>"+message.replace(/\n/g,"<br/>")+"</pre>" );
		if( $("#conversation tr").length >= 5){
			$("#conversation tr").last().remove();
		}
	}
}

function showMsgClient1(message) {
	if(message.startsWith("CLIENT at table 1")){ //Defensive programming
		console.log(message);
		if(message == "CLIENT at table 1 | I consult the menu..." || message == "CLIENT at table 1 | I drink the tea..."){
			$("#proceed1").attr("disabled", false);
		}
		if(message == "CLIENT at table 1 | MAX STAY TIME OVER!" || message == "CLIENT at table 1 | NO client"){
			$("#proceed1").attr("disabled", true);
		}
		$("#client1msg").html( "<pre>"+message.replace(/\n/g,"<br/>")+"</pre>" );
	}
}
function showMsgClient2(message) {
	if(message.startsWith("CLIENT at table 2")){ //Defensive programming
		console.log(message);
		if(message == "CLIENT at table 2 | I consult the menu..." || message == "CLIENT at table 2 | I drink the tea..."){
			$("#proceed2").attr("disabled", false);
		}
		if(message == "CLIENT at table 2 | MAX STAY TIME OVER!" || message == "CLIENT at table 2 | NO client"){
			$("#proceed2").attr("disabled", true);
		}
		$("#client2msg").html( "<pre>"+message.replace(/\n/g,"<br/>")+"</pre>" );
	}
}

function showMsgWaiterLogic(message) {
	console.log(message );
	$("#currentSituationMsg").html( "<pre id='situationPre'>"+message.replace(/\n/g,"<br/>")+"</pre>" );
	}

$(function () {
     $("form").on('submit', function (e) {
         e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });

//RING BUTTON (SOCKET.IO interaction)  
    $( "#ringOk"   ).click(function() { sendRing("36");  });
    $( "#ringSick" ).click(function() { sendRing("38");  });
    
//CLIENT SIMULATE BUTTONS (SOCKET.IO interaction)
    $( "#proceed1" ).click(function() { sendProceed("1"); });
    $( "#proceed2" ).click(function() { sendProceed("2"); });
    
    $( "#proceed1" ).attr("disabled", true);
    $( "#proceed2" ).attr("disabled", true);
});



