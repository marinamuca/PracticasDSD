var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
const SocketIOServer = require("./socketioServer.js");
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash", "json": "application/json"};

const MINTEMP = 25;
const MAXTEMP = 30;
const MINLUZ = 60;
const MAXLUZ = 80;

class HttpServer{
    constructor (db, port = 8080){
        this.port = port;
        this.db = db;
        this.server = http.createServer(
            ((request, response) => {
                var page = this.page(request.url);
                if(request.url == "/log/get"){            
                    response.writeHead(200, mimeTypes["json"]);
                    this.db.allEvents().then( (log) => {
                        this.socketio.emit("get-log", log); 
                    })
                    response.end()
                } else if( request.url == "/sensores/get" || request.url == "//get"){
                    response.writeHead(200, "text/plain");
                    this.db.ultimoPorcentajeLuz().then( (luz) => {
                        this.socketio.emit("update-luz", luz )  
                    })

                    this.db.ultimaTemperatura().then(
                            (temperatura) => {
                                this.socketio.emit("update-temperatura", temperatura )
                            }
                    )
                    this.db.aireEncendido().then(
                            (aire) => {
                                this.socketio.emit("toggle-ac", aire )
                            }
                    )
                    this.db.persianaEchada().then(
                            (persiana) => {
                                this.socketio.emit("toggle-persiana", persiana )
                            }
                    )
                    response.end()
                } else
                    this.readPage(response, page)
            })
        );
        this.socketio = new SocketIOServer(this.server)
    }

    //Mediante fs lee el html correspondiente a la peticion del cliente.
    readPage(response, fname){
        fs.readFile(fname, (err, data) => {
            if (!err) {
                var extension = fname.split(".")[1];
                var type = mimeTypes[extension];
                var code = 200;
                response.writeHead(code, type);
                response.write(data);
            }
            else {
                response.writeHead(301, { "Location": "/404" });
            }
                response.end();
        });
    }

    //A partir de la url pasada, calcula la ruta de la pagina solicitada por el cliente
    page(url){
        var ruta = ""
        switch (url) {
            case '/404':
                ruta = "pages/404.html"
                break;
            case '/log':
                ruta = "pages/log.html"
                break;
            case '/sensores': case '/':
                ruta = "pages/sensores.html"
                break;

            default:
                var peticion = url.slice(1).split("/");
                if(peticion[0] == 'sensores'){
                    console.log("Peticion recibida: " + url)
                    ruta = "pages/sensores.html"
                    var dateFormat = this.formatDate(new Date())
                    var unidad = ""
                   
                    if(peticion[1] == 'update'){    
                        var valor = parseFloat(peticion[3])
                        var tipoEvento = "update-"
                        if(peticion[2]=="luz")
                            unidad = "%";
                        else
                            unidad = "ºC"
                    }
                    if(peticion[1] == 'toggle'){
                        var valor = peticion[3]
                        var tipoEvento = "toggle-"
                    }
                    var evento = {fecha:dateFormat, tipo: peticion[2], valor: valor, unidad: unidad}; 
                    this.comprobarUmbral(peticion[2], evento.valor, dateFormat);
                    this.socketio.emit(tipoEvento + peticion[2], evento);
                    this.db.insertar("eventos", evento)
                } else {
                    ruta = path.join(process.cwd(), url);
                }
                break;
        }
        return ruta;
    }

    formatDate(date){
        return date.getDate() + "/" + date.getMonth() + "/" + date.getFullYear() + " - " + date.getHours() + ":" + ((date.getMinutes()<10?'0':'') + date.getMinutes() );
    }

    comprobarUmbral(sensor, valor){   
        var valorActual;
        if(sensor == "temperatura"){
            this.db.aireEncendido().then(
                (ac) => {
                    if(ac != null){
                        valorActual = ac.valor;
                        if( valor >= MAXTEMP && valorActual == "off"){
                            this.db.insertar("eventos", {
                                fecha:this.formatDate(new Date()),
                                tipo: "ac",
                                valor: "on"
                            }).then(
                                this.socketio.emit("alert", {actuador: "ac",mensaje: "Se enciende automaticamente el aire acondicionado.", valor: "on"}) 
                            )

                        } else if(valor <= MINTEMP  && valorActual == "on"){
                            this.db.insertar("eventos", {
                                fecha: this.formatDate(new Date()),
                                tipo: "ac",
                                valor: "off"
                            }).then(
                                this.socketio.emit("alert", {actuador: "ac", mensaje: "Se apaga automaticamente el aire acondicionado.", valor: "off"}) 
                            )
                        }
                    }
                }
            )
             
        }else{
            this.db.persianaEchada().then(
                (persiana) => {
                
                    if(persiana != null){
                        valorActual = persiana.valor;
                        if( valor >= MAXLUZ && valorActual == "off"){
                            this.db.insertar("eventos", {
                                fecha: this.formatDate(new Date()),
                                tipo: "persiana",
                                valor: "on",
                                unidad: ""

                            }).then(
                                this.socketio.emit("alert", {actuador: "persiana",mensaje: "Se baja automaticamente la persiana.", valor: "on"})   
                            )
                        } else if(valor <= MINLUZ  && valorActual == "on"){
                            this.db.insertar("eventos", {
                                fecha: this.formatDate(new Date()),
                                tipo: "persiana",
                                valor: "off",
                                unidad: ""

                            }).then(
                                this.socketio.emit("alert", {actuador: "persiana", mensaje: "Se sube automaticamente la persiana.", valor: "off"}) 
                            )
                        }
                    }
                }
            )
       
        }
    }

    /// Lanza el servidor Http
    launch() {
        
        this.server.listen(this.port);
        
        //Compruebo si el servidor se inicia o no.
        if (this.server.listening) {
            console.log("Servicio HTTP iniciado");
        } else {
            throw new Error("Error")
        }
        this.socketio.launch();
    }
}

module.exports = HttpServer;
