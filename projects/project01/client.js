const net = require('net')
const fs = require('fs')
let readline = require('readline')

const host = 'localhost'
const port = 9381
let dataPort = port

const controlSocket = new net.Socket()
controlSocket.setEncoding('utf8')

process.stdin.setEncoding('utf8')

let io = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    prompt: '>'
})

io.prompt()

io.on('line', (line) => {

    let commandTokens = line.split(' ')
    let command = commandTokens[0]
    if (commandTokens.length > 1) filePath = commandTokens[1]

    switch (command) {

        // Connect to the FTP server
        case 'CONNECT':
            controlSocket.connect(port, host, () => {
                console.log('Client: Connected to server')
            })
            break

        // List all files on the server
        case 'LIST':
            dataPort += 2

            let dataServer = net.createServer((dataSocket) => {
                dataSocket.on('data', (data) => {
                    console.log(`data socket data: ${data}`)
                })

                // dataSocket.destroy()
            }).listen(dataPort, host)

            dataServer.on('listening', function () {
                console.log(`listening on ${dataPort}`);
            });
            dataServer.on('error', function (err) {
                if (err.code == 'EADDRINUSE') {
                    console.warn('Address in use, retrying...');
                    setTimeout(() => {
                        dataServer.close();
                        dataPort += 2
                        dataServer.listen(dataPort);
                    }, 1000);
                }
                else {
                    console.error(err);
                }
            });

            controlSocket.write(`${dataPort} LIST`)
            break

        // Store a file on the server
        case 'STOR':
            // TODO

            if (filePath) {
                dataPort += 2

                controlSocket.write(`${dataPort} STOR ${filePath}`)
                fs.readFile(filePath, (error, data) => {
                    if (error) console.log('There was an error reading the file')
                    else dataSocket.end(data)
                })
                dataSocket.destroy()
            } else {
                console.log('INVALID COMMAND: no filepath was entered')
            }
            break

        // Retrieve a file from the server
        case 'RETR':
            // TODO
            break

        // Close the connection
        case 'QUIT':
            controlSocket.end()
            break

        default:
            console.log(`INVALID COMMAND: '${line.trim()}'`)
            break
    }
    io.prompt()
})

controlSocket.on('close', () => console.log('Connection closed'))
