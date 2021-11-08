const express = require('express');
const router = express.Router();
const User = require('../models/user')
const Chat = require('../models/chat');
const Device = require('../models/device');
const utils = require('../utils/custom')
const path    = require("path");
const md5 = require('md5')


router.get("/cookie", (req, resp) => {
    resp.cookie("test", "nguyen van")
    resp.json({ok: "oke"})
})

router.get('/', (req, resp) => {
    //io.emit('message', "leu leu")
    resp.sendFile(path.join(__dirname+'/../index.html'))
})


router.post('/api/v1/login', async (req, resp) => {
    let data = req.body
    console.log(data)
    const user = await User.findOne({
        "$or":  [
            { "email": data.username },
            { "phonenumber": data.username },
            { "username": data.username }
        ],
        "password": md5(data.password)
    })
    if (user) {
        let token = await utils.genateToken(user.username)
        resp.status(200).json({token})
    }
    else {
        resp.status(401).json({
            status: 401, 
            message: "Login failed!"
        })
    }
})

router.post('/api/v1/register', async (req, resp) => {
    let data = req.body;

    // create userid
    data.username = utils.creatID();

    // create verification code
    let code = Math.floor((Math.random() * 1000000) + 100000);

    const user = new User({
        "username": data.username,
        "fullname": data.fullname,
        "email": data.email,
        "password": md5(data.password),
        "phonenumber": data.phonenumber,
        "gender": data.gender,
        "verify": {
            "code": code,
            "used": false
        }
    })
    try {
        const result = await user.save();
        if (data.email) {
            utils.sendMail(data.email, code);
        }
        resp.json(result);
    }
    catch {
        resp.status(400).json({
            status: 400,
            message: "An error has occurred!"
        })
    }
})

router.post('/api/v1/verification', async (req, resp) => {
    let data = req.body;
    const users = await User.find({
        email: data.email
    })
    
    

    resp.json(users)
})

router.post('/add-device', async (req, resp) => {
    let data = req.body;
    let username = utils.getUsernameFromToken(data.jwtToken);
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


module.exports = router;