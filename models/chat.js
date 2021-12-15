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
            content: String, 
            timestamp: {
                type: Date, default: Date.now()
            },
            seen: {
              type: Boolean, default: false
            }
        }
    ]
  },
  { collection: "chat" }
);

module.exports = mongoose.model("chat", chat);