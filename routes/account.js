const express = require('express');
const router = express.Router();
const User = require('../models/user')
const Chat = require('../models/chat');
const Device = require('../models/device');
const utils = require('../utils/custom')
const path    = require("path");
const md5 = require('md5')
const fetch = require('node-fetch');


router.get("/cookie", (req, resp) => {
    resp.cookie("test", "nguyen van")
    resp.json({ok: "oke"})
})

router.get('/', (req, resp) => {
    //io.emit('message', "leu leu")
    resp.sendFile(path.join(__dirname+'/../index.html'))
})

router.post('/api/v1/login-token-huawei', async (req, resp) => {
    let access_token = req.body.token
    //console.log("huawei token: " + access_token)
    const response = await fetch("https://account.cloud.huawei.com/rest.php?nsp_svc=GOpen.User.getInfo", {
        "method": "POST",
        "headers": {
          "content-type": "application/x-www-form-urlencoded"
        },
        "body": `getNickName=1&access_token=${encodeURIComponent(access_token)}`
    });
    const data = await response.json()
    //console.log("check huawei token: ", data)
    if (!data.error) {
        // check account has exist
        const user = await User.findOne({
            "username": data.openID
        })
        const token = await utils.genateToken(data.openID)
        if (!user) {
            const uMongo = new User({
                username: data.openID
            })
            await uMongo.save()
            return resp.status(200).json({
                status: 8888,
                message: "Missing Fullname",
                data: token
            })
        }
        return resp.status(200).json({
            status: 200,
            message: "Success",
            data: token
        })
    }
    else {
        return resp.status(200).json({
            status: 400,
            message: "Fail"
        })
    }

    
})



router.post('/api/v1/login', async (req, resp) => {
    let data = req.body
    console.log("sdf sda ds f",data.username)
    const user = await User.findOne({
        "$or":  [
            { "email": data.username },
            { "username": data.username }
        ],
        "password": md5(data.password)
    })
    console.log("get user " + JSON.stringify(user))
    if (user) {
        let token = await utils.genateToken(user.username)
        console.log("jwt token: ", user.username, token)
        resp.status(200).json({
            status: 200,
            message: "Success!",
            data: token
        })
    }
    else {
        //console.log("response login null")
        resp.status(200).json({
            status: 400,
            message: "Login fail!",
        })
    }
})

router.post('/api/v1/register', async (req, resp) => {
    let data = req.body;
    console.log("register", JSON.stringify(data))
    // create userid
    data.username = utils.creatID();

    // create verification code
    let code = Math.floor((Math.random() * 1000000) + 100000);

    const user = new User({
        "username": data.username,
        "fullname": data.fullname,
        "email": data.email,
        "password": md5(data.password),
        "phonenumber": data.phone_number,
        "gender": data.gender,
        "verify": {
            "code": code,
            "used": false
        }
    })
    try {
        const result = await user.save();
        // if (data.email) {
        //     utils.sendMail(data.email, code);
        // }
        resp.json({
            status: 200,
            message: "Success!"
        });
    }
    catch {
        resp.status(200).json({
            status: 400,
            message: "An error has occurred!"
        })
    }
})


router.post('/api/v1/user/update', async (req, resp) => {
    let username = await utils.getUsernameFromToken(req.body.token)
    if (username) {
        const user = await User.findOne({
            "username": username
        })
        user.fullname = req.body.fullname
        await new User(user).save()
        resp.json({
            status: 200,
            message: "Success!"
        });
    }
    else
        resp.status(200).json({
            status: 400,
            message: "An error has occurred!"
        })
    
})

router.post('/api/v1/authentication', async (req, resp) => {
    let data = req.body;
    let username = await utils.getUsernameFromToken(data.token)
    //console.log("authen", username)
    if (username) {
        const user = await User.findOne({
            "$or":  [
                { "email": username },
                { "phonenumber": username },
                { "username": username }
            ]
        })
        
        if (user) {
            //console.log(JSON.stringify(user))
            if (user.fullname)
                return resp.status(200).json({
                    status: 200,
                    message: "Ok",
                    data: "Oke"
                })
            else
                return resp.status(200).json({
                    status: 8888,
                    message: "Ok",
                    data: "Oke"
                })
        }
    }


    return resp.status(200).json({
        status: 400,
        message: "Token fail",
        data: "Verify token fail"
    })
    
})

router.post('/api/v1/add-device', async (req, resp) => {
    let data = req.body;
    //console.log("add debvice", data)
    let username = await utils.getUsernameFromToken(data.jwt_token);
    //console.log("username", username)
    if (username) {
        let device = await Device.findOne({
            "username": username
        })
        //console.log("query device", device)
        if (!device) {
            device = {
                "username": username,
                "tokens": [data.device_token]
            }
        }
        else {
            device.tokens.push(data.device_token)
        }
        
        device.tokens = [...new Set(device.tokens)];
        await new Device(device).save()
        return resp.status(200).json({
            status: 200,
            message: "Success!"
        })
    }
    else {
        return resp.status(200).json({
            status: 401,
            message: "Unauthorized!"
        })
    }
})


module.exports = router;