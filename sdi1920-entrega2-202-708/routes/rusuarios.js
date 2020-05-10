module.exports = function (app, swig, gestorBD, gestor) {

    app.get("/usuarios/lista", function (req, res) {

        let criterio = {};
        if (req.query.busqueda != null) {
            criterio = {$or: [{"name": {$regex: ".*" + req.query.busqueda + ".*"}}, {"surname": {$regex: ".*" + req.query.busqueda + ".*"}}, {"email": {$regex: ".*" + req.query.busqueda + ".*"}}]};
        }

        let pg = parseInt(req.query.pg); // Es String !!!
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }

        gestor.obtenerObjetosPg(criterio, 'usuariosentrega2', pg, function () {
            res.send("Error al listar ");
        }, function (objetos, paginas, pg) {
            let respuesta = swig.renderFile('views/buserlist.html',
                {
                    logged: req.session.usuario,
                    usuarios: objetos,
                    paginas: paginas,
                    actual: pg,
                    busqueda: req.query.busqueda
                });
            res.send(respuesta);
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
            gestor.obtenerObjetos(criterio, 'usuariosentrega2',
                function () {
                    let usuario = {
                        email: req.body.email,
                        name: req.body.name,
                        surname: req.body.surname,
                        password: seguro
                    };
                    gestor.insertarObjetos(usuario, "usuariosentrega2", function () {
                        res.redirect("/registrarse?mensaje=Error al registrar usuario&tipoMensaje=alert-danger ");
                    }, function () {
                        res.redirect("/identificarse?mensaje=Nuevo usuario registrado");
                    });

                }, function (objetos) {
                    res.redirect("/registrarse?mensaje=Ya está existe un usuario registrado con ese email. Por favor utilice otro&tipoMensaje=alert-danger ");
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

        gestor.obtenerObjetos(criterio, 'usuariosentrega2',
            function () {
                req.session.usuario = null;
                res.redirect("/identificarse?mensaje=Email o password incorrecto&tipoMensaje=alert-danger ");
            }, function (objetos) {
                req.session.usuario = objetos[0].email;
                res.redirect("/usuarios/lista")
            })
    });

};
