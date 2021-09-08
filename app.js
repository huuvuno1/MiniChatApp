const express = require('express')
const app = express()
const http = require('http')
const server = http.createServer(app)
const { Server } = require("socket.io");
const io = new Server(server);
const jwt = require('jsonwebtoken')
const mongoose = require('mongoose');
const User = require('./models/user')
const Chat = require('./models/chat');
const Device = require('./models/device');
const e = require('cors');
const SECRET_KEY = 'djkajfkjadskjfklsjdaklfjajwlkjekljklja';
const md5 = require('md5');
const nodemailer = require('nodemailer');



//var mongoDB = 'mongodb+srv://nguyenhuuvu:EfAa2AjPQ.QeCmp@cluster0.6ogoa.mongodb.net/test?retryWrites=true&w=majority';
var mongoDB = 'mongodb://localhost:27017/chat?retryWrites=true&w=majority';
mongoose.connect(mongoDB, {useNewUrlParser: true, useUnifiedTopology: true}).then(async () => {
});

var db = mongoose.connection;

db.on('error', console.error.bind(console, 'MongoDB connection error:'));



// RESTFUL API
app.use(express.json())

app.post('/login', async (req, resp) => {
    let data = req.body
    const user = await User.findOne({
        "$or":  [
            { "email": data.username },
            { "phonenumber": data.username },
            { "username": data.username }
        ],
        "password": md5(data.password)
    })
    if (user) {
        let token = await genateToken(user.username)
        resp.status(200).json({token})
    }
    else {
        resp.status(401).json({
            status: 401, 
            message: "Login failed!"
        })
    }
})

app.post('/register', async (req, resp) => {
    let data = req.body;
    data.username = creatID();
    const user = new User({
        "username": data.username,
        "fullname": data.fullname,
        "email": data.email,
        "password": md5(data.password),
        "phonenumber": data.phonenumber,
        "gender": data.gender
    })
    try {
        const result = await user.save();
        resp.json(result);
    }
    catch {
        resp.status(400).json({
            status: 400,
            message: "An error has occurred!";
        })
    }
})


app.post('/add-device', async (req, resp) => {
    let data = req.body;
    let username = getUsernameFromToken(data.jwtToken);
    if (username) {
        let device = Device.findOne({
            "username": username
        })

        if (!device) {
            device = new Device({
                "username": username,
                "tokens": []
            })
        }

        // remove old device token
        let i = device.tokens.indexOf(data.oldDeviceToken)
        if (i >= 0)
            device.tokens.splice(i, 1);
        
        // add new device token
        device.tokens.push(newDeviceToken)
        await device.save();
        return resp.status(200).json({
            status: 200,
            message: "Success!"
        })
    }
    else {
        return resp.status(401).json({
            status: 401,
            message: "Unauthorized!"
        })
    }
})





// SOCKET.IO
server.listen(3000, () => {
    console.log('server has started')
})

io.on('connection', (socket) => {
    console.log('have a user connect + ' + socket.id)


    

    socket.on('status', async token => {
        let username = await getUsernameFromToken(token)
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
const devices = new Map();


async function sendNotication(url = '', data = {}) {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });
    return response.json();
}

async function getUsernameFromToken(token) {
    try {
        var decoded = await jwt.verify(token, SECRET_KEY);
        if (decoded.exp > Date.now() / 1000)
            return decoded.username;
      } catch(err) {
    }
    return null
}

async function genateToken(username) {
    return jwt.sign({
        exp: Math.floor(Date.now() / 1000) + (60 * 60 * 24 * 7),
        username: username
    }, SECRET_KEY);
}

function creatID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

function sendMail(receiver, code) {
    var transporter = nodemailer.createTransport({
        service: 'gmail',
        auth: {
          user: 'nguyenhuuvu.work@gmail.com',
          pass: 'ekpyxwrwrpsqlmcq'
        }
      });
      
      var mailOptions = {
        from: 'Verify',
        to: receiver,
        subject: 'Mã xác minh tài khoản Mini Chat',
        text: 'That was easy!'
      };
      
      transporter.sendMail(mailOptions, function(error, info){
        if (error) {
          console.log(error);
        } else {
          console.log('Email sent: ' + info.response);
        }
      });
}