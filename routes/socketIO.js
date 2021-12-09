
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


            socket.on('auth_when_start', async jwtToken => {
                let username = await utils.getUsernameFromToken(jwtToken)
                ////console.log("authen", username)
                if (username) {
                    const user = await User.findOne({
                        "$or":  [
                            { "email": username },
                            { "phonenumber": username },
                            { "username": username }
                        ]
                    })
                    
                    if (user) {
                        ////console.log(JSON.stringify(user))
                        if (user.fullname)
                            io.to(`${socket.id}`).emit("auth_when_start", "OK");
                        else
                            io.to(`${socket.id}`).emit("auth_when_start", "MISSING_NAME");
                    }
                }
                else
                    io.to(`${socket.id}`).emit("auth_when_start", "FAIL");
            })

            socket.on('register', async jwtToken => {
                let username = await utils.getUsernameFromToken(jwtToken)
                if (username) {
                    let user = await User.findOne({
                        'username': username
                    })
                    if (user) {
                        // save status for user
                        user.online = true;
                        await new User(user).save()

                        // emit all users
                        io.emit('status_user', JSON.stringify(user))

                        //console.log("user info", user.fullname)
                        user.password = ''
                        io.to(`${socket.id}`).emit('my_info', JSON.stringify(user))

                        socket.username = username
                        //console.log('username', socket.username, username)
                        let ids = userOnline.get(username)
                        if (ids) {
                            ids.add(socket.id)
                        }
                        else {
                            ids = new Set()
                            ids.add(socket.id)
                            userOnline.set(username, ids)
                        }

                        //
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
                // if (users) {
                //     users.map(u => {
                //         u._id = ''
                //         u.password = ''
                //         u.verify = ''
                //         return u
                //     })
                // }
                //console.log("response fetch", JSON.stringify(users))
                io.to(`${socket.id}`).emit('fetch_all_user', JSON.stringify(users))
            })

            
            socket.on('fetch_user_chat', async () => {
                const chats = await Chat.find({
                    "members": {
                        $in: [socket.username]
                    }
                })
                
                const users = await User.find()
                // //console.log("chat user", chats, socket.username)
                const result = []
                chats.forEach(chat => {
                    const partner_username = chat.members[0] == socket.username ? chat.members[1] : chat.members[0]
                    const user = users.filter(u => u.username == partner_username)[0]
                    const lastestMessage = chat.messages.pop()
                    const user_r = {...user._doc, content: lastestMessage.content, timestamp: lastestMessage.timestamp}
                   // user.content = 
                    //console.log("user detail chat", user)
                    result.push(user_r)
                })
                

                // //console.log("fetch user chat from main", result)
                io.to(`${socket.id}`).emit('fetch_user_chat', JSON.stringify(result))

            })

            // fetch all message
            socket.on('fetch_chat_history', async usernameFriend => {
                //console.log('fetch chat with user: ', usernameFriend)
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

                //console.log("fetch chat list: ", chat)
                io.to(`${socket.id}`).emit('fetch_chat_history', JSON.stringify(chat))
            })

            // search
            socket.on('search_user', async key => {
                const users = await User.find({
                    "$or": [
                        {'fullname': {"$regex" : key}},
                        {'email': key}
                    ]
                })
                //console.log("search", users)
                io.to(`${socket.id}`).emit('search_user', JSON.stringify(users))
            })

            socket.on('send_chat', async data => {
                data = JSON.parse(data)
                // //console.log(data)
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

                // get info usersend
                const uSend = await User.findOne({
                    'username': socket.username
                })

                let mess = {
                    sender: socket.username,
                    content: data.content, 
                    timestamp: new Date(),
                    username: socket.username,
                    fullname: uSend.fullname,
                    online: uSend.online
                }
                chat.messages.push(mess)
                //console.log("chat", chat)
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
            

            socket.on('on_typing', data => {
                //console.log(data)
                data = JSON.parse(data)
                const ids = userOnline.get(data.receiver)
                if (ids) {
                    ids.forEach(id => {
                        io.to(id).emit('on_typing', JSON.stringify({
                            'sender': socket.username,
                            'typing': data.typing
                        }))
                    })
                }
                
            })

            // disconnect: remove id from userOnline - remove token device
            socket.on('disconnect', async () => {
                //console.log("user " + socket.id + " disconnect")
                let ids = userOnline.get(socket.username)
                if (ids) {
                    ids.delete(socket.id)
                    if (ids.size == 0)
                        userOnline.delete(socket.username)
                }
                const user = await User.findOne({
                    'username': socket.username
                })

                if (user) {
                    user.online = false
                    await new User(user).save()
                    io.emit('status_user', JSON.stringify(user))
                    //console.log("disconnect", user)
                }

                console.log("user then disconnect", userOnline)
            })

            socket.on('logout', async deviceToken => {
                const device = await Device.findOne({
                    'username': socket.username
                })

                //console.log("logout: ", device, deviceToken)
            
                if (device) {
                    let i = device.tokens.indexOf(deviceToken)
                    if (i >= 0)
                        device.tokens.splice(i, 1)
                }

                await new Device(device).save()
            })


            // delete chat history (both of them lost their messages)
            socket.on('delete_chat_history', async username => {
                console.log('delete chat with user: ' + username)
                const result = await Chat.deleteOne({
                    'members': [socket.username, username]
                })

                if (result.deletedCount == 0) {
                    result = await Chat.deleteOne({
                        'members': [username, socket.username]
                    })
                }

                //io.to(`${socket.id}`).emit(JSON.stringify(user_chat))
                
                let ids = userOnline.get(username)
                if (!ids) return
                ids.forEach(idSocket => {
                    io.to(idSocket).emit('status_user', JSON.stringify(user_chat))
                })
            })

            // update profile
            socket.on('update_profile', async new_info => {
                const user = await User.findOne({
                    "username": socket.username
                })

                await new User.save(user)
                io.to(`${socket.id}`).emit('update_username', "OK")
            })

            // search message

            // xem ai nhan tin nhieu nhat
            socket.on('top_contact', async () => {
                
            })

            // get my profile
            socket.on('fetch_my_profile', async () => {
                const user = await User.findOne({
                    "username": socket.username
                })

                console.log("fetch user profile", user)

                io.to(`${socket.id}`).emit("fetch_my_profile", JSON.stringify(user))
            })


            // update profile
            socket.on('update_profile', async data => {
                console.log(data)

                let user = await User.findOne({
                    "username": socket.username
                })
                console.log(user)

                data = JSON.parse(data)
                user.fullname = data.fullname
                user.email = data.email
                await new User(user).save();
                io.to(`${socket.id}`).emit("update_profile", "OK")
            })
        })
    }
} 
