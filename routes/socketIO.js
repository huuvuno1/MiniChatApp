
const utils = require('../utils/custom')
const User = require('../models/user');
const Chat = require('../models/chat');
const Device = require('../models/device');
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

            
            socket.on('fetch_user_chat', async () => {
                const chats = await Chat.find({
                    "members": {
                        $in: [socket.username]
                    }
                })
                
                const users = await User.find()
                // console.log("chat user", chats, socket.username)
                const result = []
                chats.forEach(chat => {
                    const partner_username = chat.members[0] == socket.username ? chat.members[1] : chat.members[0]
                    const user = users.filter(u => u.username == partner_username)[0]
                    const user_r = {...user._doc, content: chat.messages.pop().content}
                   // user.content = 
                    console.log("user detail chat", user)
                    result.push(user_r)
                })
                

                // console.log("fetch user chat from main", result)
                io.to(`${socket.id}`).emit('fetch_user_chat', JSON.stringify(result))

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

                // let chat = await Chat.findOne({
                //     'members': {
                //         $all: [socket.username, usernameFriend]
                //     }
                // })

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
                    timestamp: Date.now(),
                    username: socket.username
                }
                chat.messages.push(mess)
                console.log("chat", chat)
                await new Chat(chat).save()

                // push notification
                utils.pushNotification(socket.username, data.receiver, data.content)

                // send to partner
                let ids = userOnline.get(data.receiver)
                if (!ids) return
                ids.forEach(idSocket => {
                    io.to(idSocket).emit('receive_message', JSON.stringify(mess))
                    io.to(idSocket).emit('update_content_user_chat', JSON.stringify(mess))
                })
                
                
            })
            

            socket.on('user', data => {
                console.log(data)
            })

            // disconnect: remove id from userOnline - remove token device
            socket.on('disconnect', async () => {
                console.log("user " + socket.id + " disconnect")
                let ids = userOnline.get(socket.username)
                if (ids) {
                    ids.delete(socket.id)
                    if (ids.size == 0)
                        userOnline.delete(socket.username)
                }

                console.log("user then disconnect", userOnline)
            })

            socket.on('logout', async deviceToken => {
                const device = await Device.findOne({
                    'username': socket.username
                })

                console.log("logout: ", device, deviceToken)
            
                if (device) {
                    let i = device.tokens.indexOf(deviceToken)
                    if (i >= 0)
                        device.tokens.splice(i, 1)
                }

                await new Device(device).save()
            })
        })
    }
} 
