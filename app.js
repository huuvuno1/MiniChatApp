const express = require('express')
const app = express()
const http = require('http')
const server = http.createServer(app)
const { Server } = require("socket.io");
const account = require('./routes/account')
const io = new Server(server);
const mongoose = require('mongoose');
const MySocket = require("./routes/socketIO")
const cors = require('cors');



//var mongoDB = 'mongodb+srv://nguyenhuuvu:EfAa2AjPQ.QeCmp@cluster0.6ogoa.mongodb.net/test?retryWrites=true&w=majority';
const mongoDB = 'mongodb://localhost:27017/chat?retryWrites=true&w=majority';
mongoose.connect(mongoDB, {useNewUrlParser: true, useUnifiedTopology: true}).then(async () => {
});

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error:'));



// RESTFUL API
app.use(cors());
app.use(express.json())
app.use("/", account)


server.listen(3000, () => {
    console.log('server has started')
})

MySocket.set(io)








