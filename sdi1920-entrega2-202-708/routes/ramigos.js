module.exports = function (app, swig, gestorBD, gestor) {

    app.get("/amigo/agregar/:id", function (req, res) {
        let friendEmail = req.params.id;
        if (friendEmail === req.session.usuario) {
            res.redirect("/usuarios/lista?mensaje=No puede enviar una peticion de amistad a sí mismo&tipoMensaje=alert-danger ");
        } else {
            let criterio = {to: friendEmail, from: req.session.usuario};
            gestor.comprobarAmistad(friendEmail, req.session.usuario,
                function () {
                    gestor.obtenerObjetos(criterio, 'peticionesamistad',
                        function () {
                            let peticion = {
                                to: friendEmail,
                                from: req.session.usuario,
                            };
                            gestor.insertarObjetos(peticion, 'peticionesamistad', function () {
                                res.redirect("/usuarios/lista?mensaje=Error al registrar usuario&tipoMensaje=alert-danger");
                            }, function () {
                                res.redirect("/usuarios/lista?mensaje=Peticion enviada");
                            })
                        }, function (objetos) {
                            res.redirect("/usuarios/lista?mensaje=Ya ha enviado una peticion de amistad a este usuario&tipoMensaje=alert-danger ");
                        });
                }, function () {
                    res.redirect("/usuarios/lista?mensaje=Ya eres amigo de este usuario&tipoMensaje=alert-danger ");
                });
        }
    });

    app.get("/amigo/peticiones/lista", function (req, res) {

        let criterio = {to: req.session.usuario};

        let pg = parseInt(req.query.pg); // Es String !!!
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }

        gestor.obtenerObjetosPg(criterio, 'peticionesamistad', pg,
            function () {
                res.send("Error al listar ");
            },
            function (objetos, paginas, pg) {
                let amigosLista = [];
                for (let i = 0; i < objetos.length; i++) {
                    amigosLista.push(objetos[i].from)
                }
                gestor.obtenerInformacionObjetosPg(objetos, paginas, pg, amigosLista, 'bfriendrequestlist', res, req, function () {
                    res.redirect("/amigo/peticiones/lista?mensaje=Ha ocurrido un error&tipoMensaje=alert-danger ");
                });
            });
    });

    app.get("/amigo/aceptar/:id", function (req, res) {
        let peticion = {to: req.session.usuario, from: req.params.id};
        eliminarPeticion(res, req, peticion, function (criterio) {
            let amistad = {
                amigo1: criterio.to,
                amigo2: criterio.from
            };

            gestor.insertarObjetos(amistad, 'amigos', function () {
                res.redirect("/amigo/peticiones/lista?mensaje=Se ha producido un error&tipoMensaje=alert-danger ");
            }, function () {
                res.redirect("/amigo/peticiones/lista?mensaje=Se ha aceptado la peticion");
            })
        })
    });

    app.get("/amigo/lista/", function (req, res) {

        let usuario_actual = req.session.usuario;
        let criterio = {$or: [{amigo1: usuario_actual}, {amigo2: usuario_actual}]};

        let pg = parseInt(req.query.pg); // Es String !!!
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }

        gestor.obtenerObjetosPg(criterio, 'amigos', pg,
            function () {
                res.send("Error al listar ");
            },
            function (objetos, paginas, pg) {
                let amigosLista = [];
                for (let i = 0; i < objetos.length; i++) {
                    if (objetos[i].amigo1 === usuario_actual) {
                        amigosLista.push(objetos[i].amigo2)
                    }
                    if (objetos[i].amigo2 === usuario_actual) {
                        amigosLista.push(objetos[i].amigo1)
                    }
                }
                gestor.obtenerInformacionObjetosPg(objetos, paginas, pg, amigosLista, 'bamigoslista', res, req, function () {
                    res.redirect("/amigo/lista?mensaje=Ha ocurrido un error&tipoMensaje=alert-danger ");
                });
            });
    });

    function eliminarPeticion(res, req, criterio, callback) {
        gestorBD.eliminarPeticionAmistad(criterio, function (peticiones) {
            if (peticiones == null) {
                res.redirect("/amigo/peticiones/lista?mensaje=Ya ha enviado una peticion de amistad a este usuario&tipoMensaje=alert-danger ");
            } else {
                callback(criterio);
            }
        });
    }

};
