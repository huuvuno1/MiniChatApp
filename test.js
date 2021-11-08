const express = require('express')
const app = express()
const http = require('http')
const server = http.createServer(app)
const { Server } = require("socket.io");
const account = require('./routes/account')
const io = new Server(server);
const mongoose = require('mongoose');
const MySocket = require("./routes/socketIO")
const cors = require('cors');

// Array.prototype.reducevu = function(callback, result) {

//     if (arguments.length < 2) {
//         result = 0
//     }
//     for (let i = 0; i < this.length; i++) {
//         result = callback(result, this[i], i, this)
//     }

//     return result;
// }

// const arr = [
//     {
//         name: "vu",
//         age: 19
//     }


// ]
// const result = arr.reducevu((total, e) => {
//     return total + e.age;
// }, 0)


// const arr2 = [1, 2, 3,4, 5]

// Array.prototype.mapvu = function(callback) {
//     for (let i = 0; i < this.length; i++) {
//         this[i] = callback(this[i])
//     }
// }
// arr2.mapvu(e => e * 2)
// console.log(arr2)


// Array.prototype.filtervu = function(callback) {
//     let newArr = []
//     for (let i = 0; i < this.length; i++) {
//         if (callback(this[i], i, this)) {
//             newArr.push(this[i])
//         }
//     }
//     return newArr
// }


// const newArr = arr2.filter()
// console.log("my filter", newArr)




async function run() {
    fetch("https://tutorialzine.com/misc/files/example.json")
}
run()
// callback -> promise -> asyn & await


