module.exports = {
    mongo: null,
    app: null,
    init: function (app, mongo) {
        this.mongo = mongo;
        this.app = app;
    },
    eliminarPeticionAmistad: function (criterio, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                funcionCallback(null);
            } else {
                let collection = db.collection('peticionesamistad');
                collection.remove(criterio, function (err, result) {
                    if (err) {
                        funcionCallback(null);
                    } else {
                        funcionCallback(result);
                    }
                    db.close();
                });
            }
        });
    },
    obtenerObjetos: function (criterio, coleccion, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                funcionCallback(null);
            } else {
                let collection = db.collection(coleccion);
                collection.find(criterio).toArray(function (err, objetos) {
                    if (err) {
                        funcionCallback(null);
                    } else {
                        funcionCallback(objetos);
                    }
                    db.close();
                });
            }
        });
    },
    insertarObjeto: function (objeto, coleccion, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                funcionCallback(null);
            } else {
                let collection = db.collection(coleccion);
                collection.insert(objeto, function (err, result) {
                    if (err) {
                        funcionCallback(null);
                    } else {
                        funcionCallback(result.ops[0]._id);
                    }
                    db.close();
                });
            }
        });
    },
    obtenerObjetosPg: function (criterio,coleccion, pg, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                funcionCallback(null);
            } else {
                let collection = db.collection(coleccion);
                collection.count(function (err, count) {
                    collection.find(criterio).skip((pg - 1) * 5).limit(5)
                        .toArray(function (err, amigos) {
                            if (err) {
                                funcionCallback(null);
                            } else {
                                funcionCallback(amigos, count);
                            }
                            db.close();
                        });
                });
            }
        });
    },
    modificarObjeto: function (criterio, coleccion ,mensaje, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                funcionCallback(null);
            } else {
                let collection = db.collection(coleccion);
                collection.update(criterio, {$set: mensaje}, function (err, result) {
                    if (err) {
                        funcionCallback(null);
                    } else {
                        funcionCallback(result);
                    }
                    db.close();
                });
            }
        });
    }

};