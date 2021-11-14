
const nodemailer = require('nodemailer');
const Device = require('../models/device');
const SECRET_KEY = 'djkajfkjadskjfklsjdaklfjajwlkjekljklja';
const jwt = require('jsonwebtoken')
const fetch = require('node-fetch');
const User = require('../models/user');


module.exports = {
    sendMail: function (receiver, code) {
        var transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
              user: 'nguyenhuuvu.work@gmail.com',
              pass: 'ekpyxwrwrpsqlmcq'
            }
          });
          
          var mailOptions = {
            from: 'nguyenhuuvu.work@gmail.com',
            to: receiver,
            subject: 'Xác minh tài khoản Mini Chat',
            text: 'Mã xác minh tài khoản của bạn là: ' + code
          };
          
          transporter.sendMail(mailOptions, function(error, info){
            if (error) {
              console.log(error);
            } else {
              console.log('Email sent: ' + info.response + " - To: " + receiver);
            }
          });
    },

    creatID: function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
    },

    genateToken: async function (username) {
        return jwt.sign({
            exp: Math.floor(Date.now() / 1000) + (60 * 60 * 24 * 7),
            username: username
        }, SECRET_KEY);
    },

    getUsernameFromToken: async function (token) {
          try {
              var decoded = await jwt.verify(token, SECRET_KEY);
              if (decoded.exp > Date.now() / 1000)
                  return decoded.username;
            } catch(err) {
          }
          return null
      },
      
      sendNotication: async function (url = '', data = {}) {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });
      return response.json();
      
  },
  pushNotification: async function(sender, receiver, message) {
    console.log("vao push notifi")
    const device = await Device.findOne({
      'username': receiver
    })

    if (!device)
      return

    // get access token for server
    const response = await fetch("https://oauth-login.cloud.huawei.com/oauth2/v3/token", {
      "headers": {
        "content-type": "application/x-www-form-urlencoded"
      },
      "referrerPolicy": "strict-origin-when-cross-origin",
      "body": "grant_type=client_credentials&client_id=104917277&client_secret=9bf5e3dfb5e1007aa44358f8ed537c52a42040ced9c663098d66094702116e22",
      "method": "POST"
    });

    const data = await response.json()

    const userSend = await User.findOne({
      'username': sender
    })

    // push noti to receiver's device 
    const response_m = await fetch("https://push-api.cloud.huawei.com/v1/104917277/messages:send", {
      "headers": {
        "authorization": `${data.token_type} ${data.access_token}`,
        "content-type": "application/x-www-form-urlencoded"
      },
      "body": JSON.stringify({
        "validate_only": false,
        "message": {
            "data": `{'title':'${userSend.fullname}','body':'${message}'}`,
            "token": device.tokens
        }
    }),
      "method": "POST"
    });

    const data_m = await response_m.json();
    console.log("push noti", data_m)
  }

    
}