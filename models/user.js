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
    }
  },
  { collection: "user" }
);

module.exports = mongoose.model("user", user);