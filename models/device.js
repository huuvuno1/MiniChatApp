const mongoose = require("mongoose");

const Schema = mongoose.Schema;

let device = new Schema(
  {
    username: {
      type: String
    },
    tokens: {
      type: [String]
    }
    
  },
  { collection: "device" }
);

module.exports = mongoose.model("device", device);