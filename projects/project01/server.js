const net = require('net')
const fs = require('fs')
const path = require('path')
require('module')

const host = 'localhost'
const port = 9381

let clientNum = 0

const dir = process.cwd()

process.stdin.setEncoding('utf8')

const server = net.createServer((controlSocket) => {

    controlSocket.setEncoding('utf8')

    clientNum++
    controlSocket.nickname = `Client ${clientNum}`
    let clientName = controlSocket.nickname;

    console.log(`Server: ${clientName} has connected from ${controlSocket.remoteAddress}`)

    controlSocket.on('end', () => {
        console.log(`Server: Client ${clientName} disconnected`)
    })

    controlSocket.on('data', (data) => {
        // Parse the command.
        let dataString = data.split(' ');
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
                list(dataPort, controlSocket.remoteAddress)
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

// Catch all uncaught errors and log to console.
process.on('uncaughtException', function (err) {
    console.log(err);
});


function list (port, host) {
    let dataSocket = new net.Socket()
    // dataSocket.setEncoding('utf8')
    console.log(`before dataSocket.connect(${port} ${host})`)
    dataSocket.connect(port, host, () => {
        console.log('yay connected')
        dataSocket.write('test')
    })

    // fs.readdir(dir, (error, file) => {
    //     if (error) {
    //         dataSocket.error = true
    //     } else {
    //         file.forEach((result) => {
    //             stats = fs.statSync(path.join(dir, result))
    //             dataSocket.write(`${result}\n`)
    //         })
    //     }
    // })
}

retr = (file) => {
    console.log('RETRIEVING FILES')
}

stor = (file) => {
    console.log('STORING FILES')
}
