module.exports = function (app, gestorBD) {

    app.get("/api/amigos/conversacion/:id", function (req, res) {
        let usuario_actual = res.usuario;
        let usuario_amigo = req.params.id;
        let criterio = {$or: [{emisor: usuario_actual,destino: usuario_amigo}, {emisor: usuario_amigo,destino: usuario_actual}]};

        gestorBD.obtenerMensaje(criterio, function (mensajes) {
            if (mensajes == null) {
                res.status(500);
                res.json({
                    error: "No hay mensajes"
                })
            } else {
                res.status(200);
                res.send(JSON.stringify(mensajes));
            }
        });
    });

    app.get("/api/amigos/lista", function (req, res) {
        let usuario_actual = res.usuario;
        let criterio = {$or: [{amigo1: usuario_actual}, {amigo2: usuario_actual}]};
        gestorBD.obtenerAmigos(criterio, function (amigos) {
            if (amigos == null) {
                res.status(500);
                res.json({
                    error: "se ha producido un error"
                })
            } else {
                let amigosLista = [];
                for (let i = 0; i < amigos.length; i++) {
                    if (amigos[i].amigo1 === usuario_actual) {
                        amigosLista.push(amigos[i].amigo2)
                    }
                    if (amigos[i].amigo2 === usuario_actual) {
                        amigosLista.push(amigos[i].amigo1)
                    }
                }
                res.status(200);
                res.send(JSON.stringify(amigosLista));
            }
        });
    });

    app.put("/api/amigos/leermensaje/:id", function (req, res) {

        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};

        let mensaje = {leido: true};

        comprobarRepector(res, req, criterio, function () {
            gestorBD.modificarMensaje(criterio, mensaje, function (result) {
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
        gestorBD.obtenerMensaje(mensajeId, function (mensajes) {
            if (mensajes == null) {
                res.status(500);
                res.json({
                    error: "No hay mensajes"
                })
            } else {
                if(mensajes[0].destino !== res.usuario){
                    res.status(500);
                    res.json({
                        error: "No eres el receptor del mensaje"
                    })
                }else{
                    callback();
                }
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

        if (error.length != 0) {
            res.status(500);
            res.json({
                error: error
            })
        } else {
            comprobarAmistad(res, req, mensaje.emisor, mensaje.destino, function (from, to) {
                gestorBD.insertarMensaje(mensaje, function (id) {
                    if (id == null) {
                        res.status(500);
                        res.json({
                            error: "se ha producido un error"
                        })
                    } else {
                        res.status(201);
                        res.json({
                            mensaje: "mensaje enviado",
                            _id: id
                        })
                    }
                });
            });
        }
    });

    function comprobarAmistad(res, req, from, to, callback) {
        let criterio = {$or: [{amigo1: from,amigo2: to}, {amigo1: to, amigo2: from}]};

        gestorBD.obtenerAmigos(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length === 0) {
                res.status(500);
                res.json({
                    error: "No eres amigo del usuario destino"
                })
            } else {
                callback(from, to);
            }
        });
    }

    app.post("/api/autenticar/", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        };

        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length === 0) {
                res.status(401);
                res.json({autenticado: false});
            } else {
                var token = app.get('jwt').sign({usuario: criterio.email, tiempo: Date.now() / 1000}, "secreto");
                res.status(200);
                res.json({autenticado: true, token: token});
            }
        })

    });

};

