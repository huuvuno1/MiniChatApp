const mongoose = require("mongoose");

const Schema = mongoose.Schema;

let user = new Schema(
  {
    username: {
      type: String
    },
    fullname: {
      type: String
    },
    email: {
      type: String
    },
    phonenumber: {
        type: String
    },
    password: {
        type: String
    },
    gender: {
      type: String
    },
    avatar: {
      type: String
    },
    verify: {
      code: Number,
      used: Boolean,
      expires: {
        type: Date, default: new Date(Date.now() + 60*60*8*1000)
      }
    }
  },
  { collection: "user" }
);

module.exports = mongoose.model("user", user);