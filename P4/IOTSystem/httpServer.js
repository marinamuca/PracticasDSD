var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
const SocketIOServer = require("./socketioServer.js");
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash"};

class HttpServer{
    constructor (db, port = 8080){
        this.port = port;
        this.db = db;
        this.server = http.createServer(
            ((request, response) => {
                var page = this.page(request.url);
                this.readPage(response, page)
            })
        );
        this.socketio = new SocketIOServer(this.server)
    }

    //Mediante fs lee el html correspondiente a la peticion del cliente.
    readPage(response, fname){
        fs.readFile(fname, function (err, data) {
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
            case '/home': case '/':
                ruta = "pages/home.html"
                break;
            case '/404':
                ruta = "pages/404.html"
                break;
            case '/log':
                ruta = "pages/log.html"
                break;
            case '/sensores':
                ruta = "pages/sensores.html"
                break;
            
            default:
                var peticion = url.slice(1).split("/");
                if(peticion[0] == 'sensores'){
                    console.log("Peticion recibida: " + url)
                    ruta = "pages/sensores.html"
                    var valor = parseFloat(peticion[2])
                    var date = new Date();
                    var dateFormat = date.getDate() + "/" + date.getMonth() + "/" + date.getFullYear() + " - " + date.getHours() + ":" + ((date.getMinutes()<10?'0':'') + date.getMinutes() );
                    var evento = {fecha:dateFormat, tipo: peticion[1], valor:valor};
                    this.socketio.emit("update-" + peticion[1], evento);
                    this.db.insertar("eventos", [evento])
                } else {
                    ruta = path.join(process.cwd(), url);
                }
                break;
        }
        return ruta;
    }



    /// Lanza el servidor Http
    launch() {
        this.socketio.launch();

        this.server.listen(this.port);

        //Compruebo si el servidor se inicia o no.
        if (this.server.listening) {
            console.log("Servicio HTTP iniciado");
        } else {
            throw new Error("Error")
        }

    }
}

module.exports = HttpServer;
