var MongoClient = require('mongodb').MongoClient;
var MongoServer = require('mongodb').Server;

class DBServer {


    constructor (url, dbName){
        this.db = null;
        this.url = url;
        this.dbName = dbName;
    }

    getDB() {
        return this.db;
    }

    // insert(collection, valor){
    //     return 
    // }

    launch(){
        MongoClient.connect(this.url, {
            // useNewUrlParse: true,
            useUnifiedTopology: true
        }).then( (connection) => {
            this.db = connection.db(this.dbName)
            console.log("Mongo Client is Ready")
        } ).catch( (err) => {
            console.log("Mongo Client failed", err);
        })
        
    }
}
module.exports = DBServer;
