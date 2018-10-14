const net = require('net')
const fs = require('fs')
const path = require('path')
require('module')

const host = 'localhost'
const port = 9381

let clientNum = 0

const dir = process.cwd()

const server = net.createServer((socket) => {
<<<<<<< HEAD
    console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort}`);

    // Identify this client
    socket.name = socket.remoteAddress + ":" + socket.remotePort
=======
>>>>>>> ed3a9af73f9964624570ef52d9fb3f6e33e8dfdb

    clientNum++
    socket.nickname = `Client ${clientNum}`
    let clientName = socket.nickname;

<<<<<<< HEAD
    // socket.on('connection', () => {
    //     console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort}`);
    // })
=======
    console.log(`Server: ${clientName} has connected from ${socket.remoteAddress}`)
>>>>>>> ed3a9af73f9964624570ef52d9fb3f6e33e8dfdb

    socket.on('end', () => {
        console.log(`Server: Client ${clientName} disconnected`)
    })

    socket.on('data', (data) => {
        // Parse the command.
        // TODO: _why_ is toString necessary?!?
        let dataString = data.toString().split(' ');
        let dataPort = dataString[0]
        let command = dataString[1]
        if (dataString.length > 2) {
            let fileName = dataString[2]
            console.log(`port: ${dataPort}, command: ${command}, file: ${fileName}`)
        } else {
            console.log(`port: ${dataPort}, command: ${command}`)
        }

        switch (command) {
            case 'LIST':
<<<<<<< HEAD
                list(dataPort)
                break
=======
                list(socket, clientName)
            break
>>>>>>> ed3a9af73f9964624570ef52d9fb3f6e33e8dfdb

            case 'STOR':
                stor(fileName)
                break

            case 'RETR':
                retr(fileName)
                break

            default:
                console.log('INVALID COMMAND')
                break
        }
    })

}).listen(port, host)

console.log(`Listening at ${host} on port ${port}`)

list = (socket, clientName) => {
    console.log('list some things')
    socket.write(`You have been given client name ${clientName}`)
}

retr = (file) => {
    console.log('RETRIEVING FILES')
}

stor = (file) => {
    console.log('STORING FILES')
}