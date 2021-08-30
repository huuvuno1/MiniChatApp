const express = require('express')
const app = express()
const http = require('http')
const server = http.createServer(app)
const { Server } = require("socket.io");
const io = new Server(server);
const jwt = require('jsonwebtoken')

app.get('/', (req, resp) => {
    resp.sendFile(__dirname + '/index.html');
})

server.listen(3000, () => {
    console.log('server has started')
})

io.on('connection', (socket) => {
    console.log('have a user connect + ' + socket.id)
    socket.disconnect()

    socket.on('login', (data) => {
        // check data
        console.log(data)
        if (data.username == 'admin' && data.password == 'admin') {
            jwt.sign({username: data.username}, "daylakey", (err, token) => {
                io.sockets.emit('client', {token});
                console.log(token)
            })
        }
        else {
            socket.emit("client", "tach r")
        }
    })

    socket.on('authenticate', token => {
        let success = true;
        let username = token; // get from token
        if (success) {
            let ids = userOnline.get(username);
            if (ids)
                ids.push(socket.id);
            else {
                userOnline.set(username, [socket.id])
            }
        }
        console.log("list user", userOnline)
    })


    // disconnect
    socket.on('disconnect', () => {
        console.log("user " + socket.id + " disconnect")
        userOnline.forEach((value, key) => {
            let i = value.indexOf(socket.id);
            if (i >= 0) {
                value.splice(i, 1)
                if (value.length == 0) {
                    userOnline.delete(key)
                }
                return;
            }

        })
    })
})



const userOnline = new Map();