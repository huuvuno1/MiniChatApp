
const utils = require('../utils/custom')
const userOnline = new Map();

module.exports = {
    set: function(io) {
        
        io.on('connection', (socket) => {
            console.log('have a user connect + ' + socket.id);
            socket.on('status', async token => {
                let username = await utils.getUsernameFromToken(token)
                if (username) {
                    let ids = userOnline.get(username);
                    if (ids)
                        ids.push(socket.id);
                    else {
                        userOnline.set(username, [socket.id])
                    }
                }
                else {
                    socket.disconnect()
                }
                console.log("list user", userOnline)
            })
            
            socket.on('send-message', data => {
                let ids = userOnline.get(data.receiver);
                if (ids) {
                    ids.forEach(id => {
                        io.to(id).emit("receive-message", data);
                    })
                }
                else {
        
                }
            })
            

            socket.on('user', data => {
                console.log(data)
            })

            // disconnect
            socket.on('disconnect', () => {
                console.log("user " + socket.id + " disconnect")
                console.log(userOnline)
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
    }
} 
