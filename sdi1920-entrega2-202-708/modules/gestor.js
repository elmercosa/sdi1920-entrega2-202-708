module.exports = {
    gestorBD: null,
    swig: null,
    init: function (gestorBD,swig) {
        this.gestorBD = gestorBD;
        this.swig = swig;
    },
    obtenerObjetos: function (criterio, coleccion, error, success) {
        this.gestorBD.obtenerObjetos(criterio, coleccion, function (objetos) {
            if (objetos == null || objetos.length === 0) {
                error();
            } else {
                success(objetos);
            }
        })
    },

    insertarObjetos: function (objeto, coleccion, error, success) {
        this.gestorBD.insertarObjeto(objeto, coleccion, function (id) {
            if (id == null) {
                error();
            } else {
                success(id);
            }
        });
    },
    obtenerObjetosPg: function (criterio, coleccion, pg, error, success) {
        this.gestorBD.obtenerObjetosPg(criterio, coleccion, pg, function (objetos, total) {
            if (objetos == null) {
                error();
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
                success(objetos, paginas, pg);

            }
        })
    },

    obtenerInformacionObjetosPg: function (objetos, paginas, pg, array, view, res, req, error) {
        let swiq = this.swig;

        let criterioBusqueda = {email: {$in: array}};
        if (array.length === 0) {
            let respuesta = this.swig.renderFile('views/' + view + '.html',
                {
                    logged: req.session.usuario,
                    usuarios: [],
                    paginas: paginas,
                    actual: pg
                });
            res.send(respuesta);
        } else {
            this.obtenerObjetos(criterioBusqueda, 'usuariosentrega2',
                function () {
                    error();
                },
                function (objetos) {
                    let respuesta = swiq.renderFile('views/' + view + '.html',
                        {
                            logged: req.session.usuario,
                            usuarios: objetos,
                            paginas: paginas,
                            actual: pg
                        });
                    res.send(respuesta);
                });
        }
    },
    comprobarAmistad: function (amigo1, amigo2, error, success) {
        let criterioAmistad = {
            $or: [{amigo1: amigo1, amigo2: amigo2}, {amigo1: amigo2, amigo2: amigo1}]
        };
        this.obtenerObjetos(criterioAmistad, 'amigos',
            function () {
                success();
            },
            function () {
                error();
            });
    }
};