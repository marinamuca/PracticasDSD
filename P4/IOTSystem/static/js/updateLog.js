var socket = io();
var readlog;
window.onload = function httpGet()
{
    readlog=false
    var httpRequest = new XMLHttpRequest();
    httpRequest.open( "GET", document.URL + "/get", false ); // false for synchronous request
    httpRequest.send( null );
}

function createLi(classname, text){
    var li = document.createElement("li");
    li.classList.add(classname);
    li.innerText = text;
    return li;
}

function addEvento(data){
    var ulEventos = document.getElementById("eventos");
    var text = data.fecha + ": Cambio en " + data.tipo + " -> Nuevo valor: " + data.valor + data.unidad;
    var evento = createLi("evento", text);
    ulEventos.append(evento);
}

socket.on("get-log", function(data) {
    if(!readlog){
        data.forEach(element => {
            addEvento(element)
        });
        readlog = true;
    }
})

socket.on("update-luz", function(data){
    addEvento(data)
})

socket.on("update-temperatura", function(data){
    addEvento(data);
})
socket.on("toggle-ac", function(data){
    addEvento(data);
})
socket.on("toggle-persiana", function(data){
    addEvento(data);
})


