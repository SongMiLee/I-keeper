import socket

HOST = ''
PORT = 9999


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind( (HOST, PORT) )

print( 'Start' )


s.listen(1)
print( 'Listen' )
conn, addr = s.accept()
print( 'Accept' )

print( 'Connected by', addr )

while True:
	data = conn.recv(1024)
	if not data: break
	conn.send(data)
conn.close()
