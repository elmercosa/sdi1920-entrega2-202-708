module.exports = function (app, swig, gestorBD) {
    app.get("/home", function (req, res) {
        let respuesta = swig.renderFile('views/bhome.html', {logged: req.session.usuario != null});
        res.send(respuesta);
    });
};
