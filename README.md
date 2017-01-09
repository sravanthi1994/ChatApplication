# ChatApplication
TCP Socket Programming

===============================================================
This file contains information about an Internet chat application with a server program and a client program. The server program manages the chat room and allows any number of clients to join the chat room and start a conversation. The client should include it's name and the server port number, where the server is running and ready to take commands from the client. 
We recommend that you read this file to know more about the commands and the format to give those commands. 

===============================================================


- Running the program.

To run through a command prompt in Windows
	-Go to start menu and in search bar type 'cmd' and press 'Enter'
	-Change the working directory in cmd using 'cd' command, to the directory where the Server.java program is saved
	-Type set path="address of bin in jdk"
	-Type 'java Server ''portnumber''' to run the Server program on the port number specified in the command
	-Now open another 'cmd' for running clients. For multiple clients, open equal number of command prompts. Set the directory of every cmd to the location where Client.java is saved.
	-Type set path="address of bin in jdk"
	-Type 'java Client clientname server_port_number'

	Input the commands to perform any of the below listed functions:
	1. Broadcast message: Any client is able to send a text to the server, which will send it to all the other clients for display
	Command Format: broadcast message "type the message to be broadcasted"

	2. Broadcast file: Any client is able to send a file of any type to the group via the server 
	Command Format: broadcast file "absolute path of file" 

	3. Unicast message: Any client is able to send a private message to a specific other client via the server
	Command format: unicast message "type the message to be unicasted" clientname_of_the_receiver

	4. Unicast file: Any client is able to send a private file to a specific other client via the server	
	Command format: unicast file "type the file to be unicasted" clientname_of_the_receiver

	5. Blockcast message: Any client is able to send a text to all the other clients except for one via the server
	Command format: blockcast message "type the message to be sent" clientname_to_be_excluded

	-For the client to leave the chat room, press Ctrl+C
	-To close the server, press Ctrl+C

