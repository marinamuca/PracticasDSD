window.onload = function (){
    document.getElementById("luz-btn").addEventListener("click", submitLuz);
    document.getElementById("temp-btn").addEventListener("click", submitTemperatura);
    // Actuadores
    document.getElementById("persiana").addEventListener("click", submitPersiana);
    document.getElementById("ac").addEventListener("click", submitAC);
    getValores();
};

var inputLuz = document.getElementById("luz-input");
var inputTemperatura = document.getElementById("temp-input");

var spanLuz = document.getElementById("luz");
var spanTemperatura = document.getElementById("temperatura");

var togglePersiana = document.getElementById("persiana");
var toggleAC = document.getElementById("ac");

var socket = io();

function getValores(){
    var httpRequest = new XMLHttpRequest();
    httpRequest.open( "GET", document.URL + "/get", false ); // false for synchronous request
    httpRequest.send( null );
}

socket.on("update-luz", function(data){
    if(data != null)
        spanLuz.innerHTML = data.valor + "%";
})
socket.on("update-temperatura", function(data){
    if(data != null)
        spanTemperatura.innerHTML = data.valor + "ºC";
})

socket.on("toggle-ac", function(data){
    if(data != null){
        var estado = false;
        if(data.valor == "on")
            estado = true

        toggleAC.checked = estado
    }  
})
socket.on("toggle-persiana", function(data){
    if(data != null){
        var estado = false;
        if(data.valor == "on")
            estado = true
    
        togglePersiana.checked = estado;
    }
})

function submitForm(sensor, input, span, unidad){
    if(input.value != ""){
        var url = document.URL+"/update/"+sensor+"/"+input.value;
        var httpRequest = new XMLHttpRequest();
        httpRequest.onreadystatechange = function(){
            if(httpRequest.readyState === 4){
                if(input.value != "")
                    span.innerHTML = input.value + unidad;
            }
        };
        
        httpRequest.open("GET", url, true)
        httpRequest.send(null)
    }
   
}

function submitLuz(){
    submitForm("luz", inputLuz, spanLuz, "%")
}

function submitTemperatura(){
    submitForm("temperatura", inputTemperatura, spanTemperatura, "ºC"); 
}

function submitToggle(toggle, event){
    var modo = "off"
    if(toggle.checked) {
        modo = "on"
    } 
    var url = document.URL+"/toggle/"+event+"/"+modo;
    var httpRequest = new XMLHttpRequest();
    
    httpRequest.open("GET", url, true)
    httpRequest.send(null)
}

function submitPersiana(){
    submitToggle(togglePersiana, "persiana");
}

function submitAC(){
    submitToggle(toggleAC, "ac") 
}

function createLi(classname, text){
    var li = document.createElement("li");
    li.classList.add(classname);
    li.innerText = text;
    return li;
}

function addAlert(data){
    var ulAlert = document.getElementById("alertas");
    var evento = createLi("alerta", data);
    ulAlert.prepend(evento);
}

socket.on("alert", function(data){
    getValores();
    addAlert(data.mensaje);
    if(data.actuador == "ac"){
        if(data.valor == "on"){
            toggleAC.checked = true;
        }else{
            toggleAC.checked = false;
        }
    } else {
        if(data.valor == "on"){
            togglePersiana.checked = true;
        }else{
            togglePersiana.checked = false;
        }
    }
   
})
