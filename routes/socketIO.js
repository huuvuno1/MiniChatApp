
const utils = require('../utils/custom')
const User = require('../models/user');
const Chat = require('../models/chat')
const userOnline = new Map();
// {
//     "username_value" => ["id_socket_1", "id2_socket_2"],
//     "username_value" => ["id_socket_1", "id2_socket_2"],
// }
module.exports = {
    set: function(io) {
        io.on('connection', (socket) => {
            console.log('have a user connect + ' + socket.id);

            socket.on('register', async jwtToken => {
                let username = await utils.getUsernameFromToken(jwtToken)
                if (username) {
                    let user = await User.findOne({
                        'username': username
                    })
                    if (user) {
                        console.log("user info", user.fullname)
                        user.password = ''
                        io.to(`${socket.id}`).emit('my_info', JSON.stringify(user))

                        socket.username = username
                        console.log('username', socket.username, username)
                        let ids = userOnline.get(username)
                        if (ids) {
                            ids.add(socket.id)
                        }
                        else {
                            ids = new Set()
                            ids.add(socket.id)
                            userOnline.set(username, ids)
                        }
                    }
                    else {
                        socket.disconnect()
                    }
                }
                else {
                    socket.disconnect()
                }
                console.log("users register", userOnline)
            })

            // fetch all user, data not required
            socket.on('fetch_all_user', async () => {
                const users = await User.find()
                if (users) {
                    users.map(u => {
                        u._id = ''
                        u.password = ''
                        u.verify = ''
                        return u
                    })
                }
                console.log("response fetch", JSON.stringify(users))
                // const ids = userOnline.get(socket.username)
                // if (!ids)
                //     return;
                // ids.forEach(id => {
                //     io.to(id).emit('fetch_all_user', JSON.stringify(users))
                // })
                //io.emit('fetch_all_user', JSON.stringify(user))
                io.to(`${socket.id}`).emit('fetch_all_user', JSON.stringify(users))
            })

            
            // fetch all message
            socket.on('fetch_chat_history', async usernameFriend => {
                console.log('fetch chat with user: ', usernameFriend)
                let chat = await Chat.findOne({
                    'members': [socket.username, usernameFriend]
                })

                if (!chat) {
                    chat = await Chat.findOne({
                        'members': [usernameFriend, socket.username]
                    })
                }

                console.log("fetch chat list: ", chat)
                io.to(`${socket.id}`).emit('fetch_chat_history', JSON.stringify(chat))
            })


            socket.on('send_chat', async data => {
                data = JSON.parse(data)
                // console.log(data)
                let chat = await Chat.findOne({
                    'members': [socket.username, data.receiver]
                })
                if (!chat)
                    chat = await Chat.findOne({
                        'members': [data.receiver, socket.username]
                    })

                if (!chat) {
                    chat = {
                        members: [socket.username, data.receiver],
                        messages: []
                    }
                }
                let mess = {
                    sender: socket.username,
                    content: data.content, 
                    timestamp: Date.now()
                }
                chat.messages.push(mess)
                console.log("chat", chat)
                await new Chat(chat).save()

                // send to partner
                userOnline.get(data.receiver).forEach(idSocket => {
                    io.to(idSocket).emit('receive_message', JSON.stringify(mess))
                })
            })
            

            socket.on('user', data => {
                console.log(data)
            })

            // disconnect: remove id from userOnline
            socket.on('disconnect', () => {
                console.log("user " + socket.id + " disconnect")
                let ids = userOnline.get(socket.username)
                if (ids) {
                    ids.delete(socket.id)
                    if (ids.size == 0)
                        userOnline.delete(socket.username)
                }
                console.log("user then disconnect", userOnline)
            })
        })
    }
} 
