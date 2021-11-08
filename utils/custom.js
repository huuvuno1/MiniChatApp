
const nodemailer = require('nodemailer');
const SECRET_KEY = 'djkajfkjadskjfklsjdaklfjajwlkjekljklja';
const jwt = require('jsonwebtoken')


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
}


    
}