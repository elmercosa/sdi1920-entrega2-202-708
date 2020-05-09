module.exports = function (app, swig, gestorBD) {
    app.get("/home", function (req, res) {
        let respuesta = swig.renderFile('views/bhome.html', {logged: req.session.usuario != null});
        res.send(respuesta);
    });

    app.get("/resetbd", function (req, res) {
        gestorBD.resetBD('peticionesamistad');
        gestorBD.resetBD('amigos');
        gestorBD.resetBD('mensajes');
        gestorBD.resetBD('usuariosentrega2');
        res.send({borrado: true});
    });

};
