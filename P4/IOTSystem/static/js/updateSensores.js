window.onload = function (){
    document.getElementById("luz-btn").addEventListener("click", submitLuz);
    document.getElementById("temp-btn").addEventListener("click", submitTemperatura);
    // Actuadores
};

var inputLuz = document.getElementById("luz-input");
var inputTemperatura = document.getElementById("temp-input");

var spanLuz = document.getElementById("luz");
var spanTemperatura = document.getElementById("temperatura");

var socket = io();

socket.on("update-luz", function(data){
    spanLuz.innerHTML = data.valor + "%";
})
socket.on("update-temp", function(data){
    spanLuz.innerHTML = data.valor + "%";
})

function submitForm(sensor, input, span, unidad){
    var url = document.URL+"/"+sensor+"/"+input.value;
    var httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function(){
        if(httpRequest.readyState === 4){
            if(input.value != "")
                span.innerHTML = input.value + unidad;
            else 
                span.innerHTML = "--" + unidad
        }
    };
    
    httpRequest.open("GET", url, true)
    httpRequest.send(null)
}

function submitLuz(){
    submitForm("luz", inputLuz, spanLuz, "%")
}

function submitTemperatura(){
    submitForm("temperatura", inputTemperatura, spanTemperatura, "ºC"); 
}