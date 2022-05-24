var MongoClient = require('mongodb').MongoClient;
var MongoServer = require('mongodb').Server;

class MongoDBServer {

    constructor (url, dbName){
        this.db = null;
        this.url = url;
        this.dbName = dbName;
    }

    getDB() {
        return this.db;
    }

    async existeCollection(name){
        var existe = false;
        var collections = await this.db.listCollections().toArray();
        var collectionsNames = collections.map(c => c.name);

        if(collectionsNames.includes(name)){
            existe = true;
        }

        return existe;
    }

    async query(collection, query, sort){
        var resultado = await this.db.collection(collection).find(query).sort(sort).toArray();
        return resultado;
    }
    
    async getUltimoValor(tipo){
        var eventos = await this.allEvents();
        var valores = []
        eventos.forEach(event => {
            if(event.tipo == tipo){
               valores.push(event)
            }
        });

        return valores[0];
    }

    ultimaTemperatura(){
        return this.getUltimoValor("temperatura");
    }
    ultimoPorcentajeLuz(){
        return this.getUltimoValor("luz");
    }

    aireEncendido(){
        return this.getUltimoValor("ac");
    }
    persianaEchada(){
        return this.getUltimoValor("persiana");
    }

    async allEvents(){
        return await this.query("eventos", null, {_id: -1});
    }

    crearCollection(name){
        this.db.createCollection(name, function(err, res) {
            if(err) throw err;
            console.log("Collection created!");
        });
    }

    async insertar (collection, value){
        if(value.valor != null)
            return await this.db.collection(collection).insertOne(value);
    }

    launch(){
        MongoClient.connect(this.url, {
            useUnifiedTopology: true
        }).then( (connection) => {
            this.db = connection.db(this.dbName);
                
            console.log("Mongo Server iniciado");

            this.existeCollection("eventos").then(
                (existe) => {
                    if(!existe){
                        console.log("Creando colección de eventos..."); 
                        this.crearCollection("eventos");

                        this.insertar("eventos", {
                            fecha: "",
                            tipo: "persiana",
                            valor: "off",
                            unidad: ""
                        })
                        this.insertar("eventos", {
                            fecha: "",
                            tipo: "ac",
                            valor: "off",
                            unidad: ""
                        })
                        
                    }
                             
                }
            )
                
        } ).catch( (err) => {
            console.log("Error en Mongo Server", err);
        })    
    }
}
module.exports = MongoDBServer;
