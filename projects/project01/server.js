const net = require('net')
const fs = require('fs')
const path = require('path')
require('module')

const host = 'localhost'
const port = 9381

let clientNum = 0

const dir = process.cwd()

const server = net.createServer((socket) => {

    clientNum++
    socket.nickname = `Client ${clientNum}`
    let clientName = socket.nickname;

    console.log(`Server: ${clientName} has connected from ${socket.remoteAddress}`)

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
                list(socket, clientName)
            break

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