var app = require('express').createServer()
  , io = require('socket.io').listen(app)
  , redis = require('redis');
var sub = redis.createClient();
sub.subscribe("tags");
sub.subscribe("links");
sub.subscribe("market");
sub.subscribe("retweets");
sub.subscribe("articles");

app.listen(8080);

app.get('/', function (req, res) {
  res.sendfile(__dirname + '/index.html');
});

app.get('/readme.html', function (req, res) {
  res.sendfile(__dirname + '/readme.html');
});

io.sockets.on('connection', function (socket) {
  sub.on("message", function(pattern, key){
    socket.emit(pattern, key);
  });

});
