var http = require("http");
var url = require("url");
var fs = require("fs");
var path = require("path");
var socketio = require("socket.io");
var mimeTypes = { "html": "text/html", "jpeg": "image/jpeg", "jpg": "image/jpeg", "png": "image/png", "js": "text/javascript", "css": "text/css", "swf": "application/x-shockwave-flash"};

class HttpServer{
    constructor (port = 8080){
        this.port = port;
        this.server = http.createServer(
            ((request, response) => {
                var page = this.page(request.url);
                this.readPage(response, page)
            })
        );
    }

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

    page(url){
        var ruta = ""

        switch (url) {
            case '/home':
            case '/':
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
                ruta = path.join(process.cwd(), url);
                break;
        }
        return ruta;
    }

    start() {
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