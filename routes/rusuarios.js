module.exports = function (app, swig, gestorBD) {

    app.get("/usuarios/lista", function (req, res) {

        let criterio = {};
        if (req.query.busqueda != null) {
            criterio = { $or: [ {"name": {$regex: ".*" + req.query.busqueda + ".*"}}, {"name": {surname: ".*" + req.query.busqueda + ".*"}} ] };
        }

        let pg = parseInt(req.query.pg); // Es String !!!
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }

        gestorBD.obtenerUsuariosPg(criterio, pg, function (usuarios, total) {
            if (usuarios == null) {
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
                let respuesta = swig.renderFile('views/buserlist.html',
                    {
                        logged: req.session.usuario != null,
                        usuarios: usuarios,
                        paginas: paginas,
                        actual: pg
                    });
                res.send(respuesta);
            }
        });

    });

    app.get("/publicaciones", function (req, res) {
        let criterio = {autor: req.session.usuario};
        gestorBD.obtenerCanciones(criterio, function (canciones) {
            if (canciones == null) {
                res.send("Error al listar ");
            } else {
                let respuesta = swig.renderFile('views/bpublicaciones.html',
                    {
                        canciones: canciones
                    });
                res.send(respuesta);
            }
        });
    });

    app.get("/usuarios", function (req, res) {
        res.send("ver usuarios");
    });

    app.get("/registrarse", function (req, res) {
        let respuesta = swig.renderFile('views/bregistro.html', {});
        res.send(respuesta);
    });

    app.get('/desconectarse', function (req, res) {
        req.session.usuario = null;
        res.redirect("/home");
    });

    app.get("/identificarse", function (req, res) {
        let respuesta = swig.renderFile('views/bidentificacion.html', {});
        res.send(respuesta);
    });

    app.post('/usuario', function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        if (req.body.password === req.body.rpassword) {
            let criterio = {email: req.body.email};
            gestorBD.obtenerUsuarios(criterio, function (usuarios) {
                if (usuarios == null || usuarios.length == 0) {
                    let usuario = {
                        email: req.body.email,
                        name: req.body.name,
                        surname: req.body.surname,
                        password: seguro
                    };
                    gestorBD.insertarUsuario(usuario, function (id) {
                        if (id == null) {
                            res.redirect("/registrarse?mensaje=Error al registrar usuario&tipoMensaje=alert-danger ");
                        } else {
                            res.redirect("/identificarse?mensaje=Nuevo usuario registrado");
                        }
                    });

                } else {
                    res.redirect("/registrarse?mensaje=Ya está existe un usuario registrado con ese email. Por favor utilice otro&tipoMensaje=alert-danger ");
                }
            });
        } else {
            res.redirect("/registrarse?mensaje=Las contraseñas no coinciden&tipoMensaje=alert-danger ");
        }

    });
    app.post("/identificarse", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        };
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                req.session.usuario = null;
                res.redirect("/identificarse?mensaje=Email o password incorrecto&tipoMensaje=alert-danger ");

            } else {
                req.session.usuario = usuarios[0].email;
                res.redirect("/usuarios/lista")
            }
        });
    });

};
