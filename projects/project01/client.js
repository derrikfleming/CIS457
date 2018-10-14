const net = require('net')
const fs = require('fs')
let readline = require('readline')

const host = 'localhost'
const port = 9381
let dataPort = port

const controlSocket = new net.Socket()
// clientSocket.setEncoding('utf8')
const dataSocket = new net.Socket()
// dataSocket.setEncoding('utf8')

let io = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  prompt: '>'
})

io.on('line', (line) => {

  let commandTokens = line.split(' ')
  let command = commandTokens[0]
  if (commandTokens.length > 1) filePath = commandTokens[1]

  switch (command) {

    // Connect to the FTP server
    case 'CONNECT':
      controlSocket.connect(port, host, () => {
        console.log('Client: Connected to server')
        // controlSocket.write('Hallo Spaceboy.')
      })
      break

    // List all files on the server
    case 'LIST':
      dataPort += 2
      controlSocket.write(`${dataPort} LIST`)

      dataSocket.connect(dataPort, host, () => {
        console.log('connected')
      })


      dataSocket.destroy()
      break

    // Store a file on the server
    case 'STOR':
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

      dataPort += 2
      controlSocket.write(`${dataPort} RETR`)

      dataSocket.listen(dataPort, host)

      dataSocket.on('data', (data) => {
        // Retrieve the file from teh server
        console.log(data)
      })

      dataSocket.end()
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