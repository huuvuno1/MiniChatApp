const mongoose = require("mongoose");

const Schema = mongoose.Schema;

let chat = new Schema(
  {
    members: {
      type: [String]
    },
    messages: [
        {
            sender: String,
            message: String, 
            timestamp: {
                type: Date, default: Date.now
            }
        }
    ]
  },
  { collection: "chat" }
);

module.exports = mongoose.model("chat", chat);