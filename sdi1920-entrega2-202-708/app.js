//Módulos
let express = require('express');
let app = express();

let rest = require('request');
app.set('rest', rest);

var jwt = require('jsonwebtoken');
app.set('jwt', jwt);

app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
    // Debemos especificar todas las headers que se aceptan. Content-Type , token
    next();
});


// let fs = require('fs');
// let https = require('https');

let expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));

let crypto = require('crypto');

let fileUpload = require('express-fileupload');
app.use(fileUpload());

let mongo = require('mongodb');
let swig = require('swig');
let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

let gestorBD = require("./modules/gestorBD.js");
gestorBD.init(app, mongo);

let gestor = require("./modules/gestor.js");
gestor.init(gestorBD,swig);

var routerUsuarioToken = express.Router();
routerUsuarioToken.use(function (req, res, next) {
    // obtener el token, vía headers (opcionalmente GET y/o POST).
    var token = req.headers['token'] || req.body.token || req.query.token;
    if (token != null) {
        // verificar el token
        jwt.verify(token, 'secreto', function (err, infoToken) {
            if (err || (Date.now() / 1000 - infoToken.tiempo) > 240) {
                res.status(403); // Forbidden
                res.json({
                    acceso: false,
                    error: 'Token invalido o caducado'
                });
                // También podríamos comprobar que intoToken.usuario existe
                return;

            } else {
                // dejamos correr la petición
                res.usuario = infoToken.usuario;
                next();
            }
        });

    } else {
        res.status(403); // Forbidden
        res.json({
            acceso: false,
            mensaje: 'No hay Token'
        });
    }
});

// Aplicar routerUsuarioToken
app.use('/api/amigos/*', routerUsuarioToken);

//Logger
var log4js = require('log4js');
var logger = log4js.getLogger();
logger.level = 'debug';

// routerUsuarioSession
let routerUsuarioSession = express.Router();
routerUsuarioSession.use(function (req, res, next) {
    console.log("routerUsuarioSession");
    if (req.session.usuario) {
        logger.info("USUARIO: " + req.session.usuario + " - ACCEDE A " + req.originalUrl);
        next();
    } else {
        logger.error("[SIN AUTORIZACION] INTENTO DE ACCESO A " + req.originalUrl);
        res.redirect("/identificarse");
    }
});

//Aplicar routerUsuarioSession
app.use("/amigo/*", routerUsuarioSession);
app.use("/usuarios/lista", routerUsuarioSession);

app.use(express.static('public'));

//Variables
app.set('port', 8081);
app.set('db', 'mongodb://admin:sdi@tiendamusica-shard-00-00-wotx7.mongodb.net:27017,tiendamusica-shard-00-01-wotx7.mongodb.net:27017,tiendamusica-shard-00-02-wotx7.mongodb.net:27017/test?ssl=true&replicaSet=tiendamusica-shard-0&authSource=admin&retryWrites=true&w=majority');
app.set('clave', 'abcdefg');
app.set('crypto', crypto);

require("./routes/rusuarios.js")(app, swig, gestorBD,gestor);
require("./routes/rhome.js")(app, swig, gestorBD);
require("./routes/ramigos.js")(app, swig, gestorBD,gestor);
require("./routes/rapi.js")(app, gestorBD,gestor);


app.get('/', function (req, res) {
    res.redirect('/home');
});

app.use(function (err, req, res, next) {
    console.log("Error producido: " + err);
    if (!res.headersSent) {
        res.status(400);
        res.send("Recurso no disponible");
    }
});

//lanzar el servidor
// https.createServer({
//     key: fs.readFileSync('certificates/alice.key'),
//     cert: fs.readFileSync('certificates/alice.crt')
// }, app).listen(app.get('port'), function () {
//     console.log("Servidor activo");
// });

app.listen(app.get('port'), function () {
    console.log("Servidor activo");
});