var serviceURL = document.URL;

var spanLuz = document.getElementById("luz");
var spanTemperatura = document.getElementById("temperatura");

var inputLuz = document.getElementById("luz-input");
var inputTemperatura = document.getElementById("temp-input");

var formLuz = document.getElementById("form-luz");
var formTemperatura = document.getElementById("form-temperatura");

function submitForm(input, span, unidad){
    var url = serviceURL;
    var httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = function(){
        if(httpRequest.readyState === 4){
            span.innerHTML = input.value + unidad;
        }
    };
    httpRequest.open("GET", url, true)
    httpRequest.send(null)
}

formLuz.onsubmit = () => {  
    submitForm(inputLuz, spanLuz, "%");
}

formTemperatura.onsubmit = () => {
    submitForm(inputTemperatura, spanTemperatura, "ºC"); 
}