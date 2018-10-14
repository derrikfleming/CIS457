const net = require('net')
const fs = require('fs')
const path = require('path')
require('module')

const host = 'localhost'
const port = 9381
const clients = []

const dir = process.cwd()

const server = net.createServer((socket) => {
    console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort}`);

    // Identify this client
    socket.name = socket.remoteAddress + ":" + socket.remotePort 

    // Put this new client in the list
    clients.push(socket);
    console.log(clients)

    // socket.on('connection', () => { 
    //     console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort}`);
    // })

    socket.on('end', () => {
        console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort} disconnected`)
        clients.splice(clients.indexOf(socket), 1);
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
                list(dataPort)
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

        // Call the command.
    })

}).listen(port, host)

console.log(`Listening at ${host} on port ${port}`)

list = (socket) => {
    console.log('list some things')

    fs.readdir(dir, (error, file) => {
        if (error) {
            socket.error = true
        } else {
            file.forEach((result) => {
                stats = fs.statSync(path.join(dir, result))
                console.log(`File ${result} ${JSON.stringify(stats)}`)
                socket.write(`File ${result} ${JSON.stringify(stats)}`)
            })
        }
    })
}

retr = (file) => {
    console.log('RETRIEVING FILES')
}

stor = (file) => {
    console.log('STORING FILES')
}