module.exports = function (app, swig, gestorBD) {

    app.get("/amigo/agregar/:id", function (req, res) {
        let friendEmail = req.params.id;
        if (friendEmail === req.session.usuario) {
            res.redirect("/usuarios/lista?mensaje=No puede enviar una peticion de amistad a sí mismo&tipoMensaje=alert-danger ");
        } else {
            comprobarPeticionAmistad(res, req, req.session.usuario, friendEmail, function (from, to) {
                let busqueda = {email: from};
                gestorBD.obtenerUsuarios(busqueda, function (usuarios) {
                    if (usuarios == null || usuarios.length == 0) {
                        res.redirect("/usuarios/lista?Se ha producido un error. Por favor intentelo de nuevo mas tarde&tipoMensaje=alert-danger ");
                    } else {
                        let peticion = {
                            to: to,
                            from_name: usuarios[0].name,
                            from_surname: usuarios[0].surname,
                            from: usuarios[0].email,
                        };

                        gestorBD.insertarPeticionAmistad(peticion, function (id) {
                            if (id == null) {
                                res.redirect("/usuarios/lista?mensaje=Error al registrar usuario&tipoMensaje=alert-danger");
                            } else {
                                res.redirect("/usuarios/lista?mensaje=Peticion enviada");
                            }
                        });
                    }
                });
            });
        }
    });


    app.get("/amigo/peticiones/lista", function (req, res) {

        let criterio = {to: req.session.usuario};

        let pg = parseInt(req.query.pg); // Es String !!!
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }

        gestorBD.obtenerPeticionAmistadPg(criterio, pg, function (peticiones, total) {
            if (peticiones == null) {
                res.send("Error al listar ");
            } else {
                let ultimaPg = total / 5;
                if (total % 5 > 0) { // Sobran decimales
                    ultimaPg = ultimaPg + 1;
                }
                let paginas = []; // paginas mostrar
                for (let i = pg - 2; i <= pg + 2; i++) {
                    if (i > 0 && i <= ultimaPg) {
                        paginas.push(i);
                    }
                }
                let amigosLista = [];
                for (let i = 0; i < peticiones.length; i++) {
                    amigosLista.push(peticiones[i])
                }

                let criterioBusqueda = {email: {$in: amigosLista}};

                if(amigosLista.length === 0){
                    let respuesta = swig.renderFile('views/bfriendrequestlist.html',
                        {
                            logged: req.session.usuario,
                            usuarios: [],
                            paginas: paginas,
                            actual: pg
                        });
                    res.send(respuesta);
                }else{
                    infoUsuario(res, req, criterioBusqueda, function (usuarios) {
                        let respuesta = swig.renderFile('views/bfriendrequestlist.html',
                            {
                                logged: req.session.usuario,
                                usuarios: usuarios,
                                paginas: paginas,
                                actual: pg
                            });
                        res.send(respuesta);
                    });
                }
            }
        });

    });

    app.get("/amigo/aceptar/:id", function (req, res) {
        let peticion = {to: req.session.usuario, from: req.params.id};
        eliminarPeticion(res, req, peticion, function (criterio) {
            let amistad = {
                amigo1: criterio.to,
                amigo2: criterio.from
            };

            gestorBD.insertarAmigos(amistad, function (id) {
                if (id == null) {
                    res.redirect("/amigo/peticiones/lista?mensaje=Se ha producido un error&tipoMensaje=alert-danger ");
                } else {
                    res.redirect("/amigo/peticiones/lista?mensaje=Se ha aceptado la peticion&tipoMensaje=alert-danger ");
                }
            });
        })
    });

    app.get("/amigo/lista/", function (req, res) {

        let usuario_actual = req.session.usuario;
        let criterio = {$or: [{amigo1: usuario_actual}, {amigo2: usuario_actual}]};

        let pg = parseInt(req.query.pg); // Es String !!!
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }

        gestorBD.obtenerAmigosPg(criterio, pg, function (amigos, total) {
            if (amigos == null) {
                res.send("Error al listar ");
            } else {
                let ultimaPg = total / 5;
                if (total % 5 > 0) { // Sobran decimales
                    ultimaPg = ultimaPg + 1;
                }
                let paginas = []; // paginas mostrar
                for (let i = pg - 2; i <= pg + 2; i++) {
                    if (i > 0 && i <= ultimaPg) {
                        paginas.push(i);
                    }
                }
                let amigosLista = [];
                for (let i = 0; i < amigos.length; i++) {
                    if (amigos[i].amigo1 === usuario_actual) {
                        amigosLista.push(amigos[i].amigo2)
                    }
                    if (amigos[i].amigo2 === usuario_actual) {
                        amigosLista.push(amigos[i].amigo1)
                    }
                }

                let criterio = {email: {$in: amigosLista}};

                if(amigosLista.length === 0){
                    let respuesta = swig.renderFile('views/bamigoslista.html',
                        {
                            logged: req.session.usuario,
                            usuarios: [],
                            paginas: paginas,
                            actual: pg
                        });
                    res.send(respuesta);
                }else{
                    infoUsuario(res, req, criterio, function (usuarios) {
                        let respuesta = swig.renderFile('views/bamigoslista.html',
                            {
                                logged: req.session.usuario,
                                usuarios: usuarios,
                                paginas: paginas,
                                actual: pg
                            });
                        res.send(respuesta);
                    });
                }
            }
        });
    });

    function comprobarPeticionAmistad(res, req, from, to, callback) {
        let criterio = {to: to, from: from};

        gestorBD.obtenerPeticionAmistad(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length === 0) {
                callback(from, to);
            } else {
                res.redirect("/usuarios/lista?mensaje=Ya ha enviado una peticion de amistad a este usuario");
            }
        });
    }

    function eliminarPeticion(res, req, criterio, callback) {
        gestorBD.eliminarPeticionAmistad(criterio, function (peticiones) {
            if (peticiones == null) {
                res.redirect("/amigo/peticiones/lista?mensaje=Ya ha enviado una peticion de amistad a este usuario&tipoMensaje=alert-danger ");
            } else {
                callback(criterio);
            }
        });
    }

    function infoUsuario(res, req, criterio, callback) {
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length === 0) {
                res.redirect("/amigo/peticiones/lista?mensaje=Ha ocurrido un error&tipoMensaje=alert-danger ");
            } else {
                callback(usuarios)
            }
        });
    }

};
