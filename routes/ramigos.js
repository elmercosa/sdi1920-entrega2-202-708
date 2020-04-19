module.exports = function (app, swig, gestorBD) {

    app.get("/amigo/agregar/:id", function (req, res) {

        let criterio = {"_id": gestorBD.mongo.ObjectID(req.params.id)};

        comprobarMismoUsuario(res, req, criterio, function (friendEmail) {
            comprobarPeticionAmistad(res, req, req.session.usuario, friendEmail, function (from, to) {
                let busqueda = {email: from}
                gestorBD.obtenerUsuarios(busqueda, function (usuarios) {
                    if (usuarios == null || usuarios.length == 0) {
                        req.session.usuario = null;
                        res.redirect("/usuarios/lista?mensaje=El usuario no existe&tipoMensaje=alert-danger ");
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
        })
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
                let respuesta = swig.renderFile('views/bfriendrequestlist.html',
                    {
                        logged: req.session.usuario,
                        usuarios: peticiones,
                        paginas: paginas,
                        actual: pg
                    });
                res.send(respuesta);
            }
        });

    });

    app.get("/amigo/aceptar/:id", function (req, res) {
        console.log(req.params.id)
    });



    function comprobarMismoUsuario(res, req, criterio, callback) {
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                req.session.usuario = null;
                res.redirect("/usuarios/lista?mensaje=El usuario no existe&tipoMensaje=alert-danger ");
            } else {
                if (usuarios[0].email === req.session.usuario) {
                    res.redirect("/usuarios/lista?mensaje=No puede enviar una peticion de amistad a sí mismo&tipoMensaje=alert-danger ");
                } else {
                    callback(usuarios[0].email);
                }
            }
        });
    }

    function comprobarPeticionAmistad(res, req, from, to, callback) {
        let criterio = {to: to, from: from};
        // let criterio = { $or: [{to: to, from: from}, {to: from, from: to} ] };

        gestorBD.obtenerPeticionAmistad(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                callback(from, to);
            } else {
                res.redirect("/usuarios/lista?mensaje=Ya ha enviado una peticion de amistad a este usuario&tipoMensaje=alert-danger ");
            }
        });
    }
};
