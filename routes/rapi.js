module.exports = function (app, gestorBD, gestor) {

    function errorjson(res, mensaje) {
        res.status(500);
        res.json({
            error: mensaje
        })
    }

    function successjson(res, array) {
        res.status(200);
        res.send(JSON.stringify(array));
    }

    app.get("/api/amigos/conversacion/:id", function (req, res) {
        let usuario_actual = res.usuario;
        let usuario_amigo = req.params.id;
        let criterio = {
            $or: [{emisor: usuario_actual, destino: usuario_amigo}, {
                emisor: usuario_amigo,
                destino: usuario_actual
            }]
        };

        gestor.obtenerObjetos(criterio, 'mensajes', function () {
            errorjson(res, "Ha ocurrido un error");
        }, function (objetos) {
            successjson(res, objetos);
        });
    });

    app.get("/api/amigos/lista", function (req, res) {
        let usuario_actual = res.usuario;
        let criterio = {$or: [{amigo1: usuario_actual}, {amigo2: usuario_actual}]};
        gestor.obtenerObjetos(criterio, 'amigos', function () {
            errorjson(res, "Ha ocurrido un error");
        }, function (objetos) {
            let amigos = [];
            for (let i = 0; i < objetos.length; i++) {
                if (objetos[i].amigo1 === usuario_actual) {
                    amigos.push(objetos[i].amigo2)
                }
                if (objetos[i].amigo2 === usuario_actual) {
                    amigos.push(objetos[i].amigo1)
                }
            }
            successjson(res, amigos);
        });
    });

    app.put("/api/amigos/leermensaje/:id", function (req, res) {

        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};

        let mensaje = {leido: true};

        comprobarRepector(res, req, criterio, function () {

            gestorBD.modificarObjeto(criterio, 'mensajes', mensaje, function (result) {
                if (result == null) {
                    res.status(500);
                    res.json({
                        error: "se ha producido un error"
                    })
                } else {
                    res.status(200);
                    res.json({
                        mensaje: "mensaje leido",
                        _id: req.params.id
                    })
                }
            });
        })
    });

    function comprobarRepector(res, req, mensajeId, callback) {

        gestor.obtenerObjetos(mensajeId, 'mensaje', function () {
            errorjson(res, "No hay mensajes")
        }, function (objetos) {
            if (objetos[0].destino !== res.usuario) {
                res.status(500);
                res.json({
                    error: "No eres el receptor del mensaje"
                })
            } else {
                callback();
            }
        });

    }

    app.post("/api/amigos/nuevomensaje", function (req, res) {
        var mensaje = {
            emisor: res.usuario,
            destino: req.body.destino,
            texto: req.body.texto,
            leido: false
        };

        let error = "";

        if (req.body.destino == null || req.body.destino.trim().length < 5) {
            error += "El destinatario no es correcto";
        }
        if (req.body.texto == null || req.body.texto.trim().length === 0) {
            error += "El texto del mensaje está vacio";
        }

        if (error.length !== 0) {
            res.status(500);
            res.json({
                error: error
            })
        } else {
            gestor.comprobarAmistad(mensaje.destino, mensaje.emisor,
                function () {
                    errorjson(res, "No eres amigo del destino");
                }, function () {
                    gestor.insertarObjetos(mensaje, 'amigos', function () {
                        errorjson(res);
                    }, function (id) {
                        res.status(201);
                        res.json({mensaje: "mensaje enviado", _id: id})
                    })
                });
        }
    });


    app.post("/api/autenticar/", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        };

        gestor.obtenerObjetos(criterio, 'usuariosentrega2', function () {
            res.status(401);
            res.json({autenticado: false});
        }, function (objetos) {
            var token = app.get('jwt').sign({usuario: criterio.email, tiempo: Date.now() / 1000}, "secreto");
            res.status(200);
            res.json({autenticado: true, token: token});
        });

    });

};

