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

    crearCollection(name){
        this.db.createCollection(name, function(err, res) {
            if(err) throw err;
            console.log("Collection created!");
        });
    }

    async insertar (collection, values){
        return await this.db.collection(collection).insertMany(values);
    }

    launch(){
        MongoClient.connect(this.url, {
            useUnifiedTopology: true
        }).then( (connection) => {
            this.db = connection.db(this.dbName);
            console.log("Mongo Server iniciado");
        } ).catch( (err) => {
            console.log("Error en Mongo Server", err);
        })    
    }
}
module.exports = MongoDBServer;
