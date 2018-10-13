const net = require('net')
const fs = require('fs')
const path = require('path')
require('module')

const host = 'localhost'
const port = 9381

const dir = process.cwd()

const server = net.createServer((socket) => {
    console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort}`);

    // socket.on('connection', () => { 
    //     console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort}`);
    // })

    socket.on('end', () => {
        console.log(`Server: Client ${socket.remoteAddress} ${socket.remotePort} disconnected`)
    })

    socket.on('data', (data) => {
        // Parse the command.
        // TODO: _why_ is toString necessary?!?
        let dataString = data.toString().split(' ');
        let dataPort = dataString[0]
        let command = dataString[1]
        console.log(`port: ${dataPort}, command: ${command}`)

        // Call the command.
    })

}).listen(port, host)

console.log(`Listening at ${host} on port ${port}`)


exports.commands = {
    'LIST': (socket) => {

        console.log('list some things')

        fs.readdir(dir, (error, file) => {
            if (error) {
                socket.error = true
            } else {
                file.forEach((result) => {
                    stats = fs.statSync(path.join(dir, result))
                    console.log(`File ${result} ${JSON.stringify(stats)}`)
                    dataSocket.write(`File ${result} ${JSON.stringify(stats)}`)
                })
            }
        })
    },
    "RETR": (file) => {
        let socket = this
        socket.dataTransfer((dataSocket, finish) => {
            fs.readFile(path.join(dir, file), function (err, data) {
                console.log(data.toString())
                // handle error if file DNE
                if (err) socket.reply(501, 'Read fail')
                else {
                    dataSocket.write(data, socket.dataEncoding)
                    dataSocket.end()
                }
            });
        })
        if (socket.dataTransfer.prepare) socket.dataTransfer.queue.shift().call(socket.dataTransfer.squeue.shift())
    },
    "STOR": (file) => {
        let socket = this
        socket.dataTransfer((dataSocket, finish) => {
            let value = ""
            dataSocket.on('data', (chunk) => {
                value += chunk
            })
            dataSocket.on('end', () => {
                fs.writeFile(path.join(dir, file), value, (error) => {
                    if (error) socket.reply(501, 'Read fail')
                })
            })
        })
        if (socket.dataTransfer.prepare) socket.dataTransfer.queue.shift().call(socket.dataTransfer.squeue.shift())
    }
}
