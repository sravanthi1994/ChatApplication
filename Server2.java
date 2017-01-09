

import java.net.*;
import java.io.*;
import java.util.*;

public class Server2 {
	private static ArrayList<Socket> socket1 = new ArrayList<Socket>();
    private static ArrayList<String> user = new ArrayList<String>();

	   

	public static void main(String[] args) throws Exception {
		if(args.length != 1)
		{
			System.out.println("Please enter the port number");
			System.exit(1);
		}
		int Firstarg;
		Firstarg = Integer.parseInt(args[0]);
		System.out.println("The server is running on port "+ Firstarg);
        ServerSocket listener = new ServerSocket(Firstarg);
		
        try {
            	while(true) {
                	new Handler(listener.accept()).start();
                	
            			}
        	} 
        finally {
            	listener.close();
        	} 
 
    	}

	/**
     	* A handler thread class.  Handlers are spawned from the listening
     	* loop and are responsible for dealing with a single client's requests.
     	*/
    	private static class Handler extends Thread {
        
        	private Socket connection;
        	private DataInputStream in;	//stream read from the socket
        	private DataOutputStream out;    //stream write to the socket
        	

        	public Handler(Socket connection) {
            	this.connection = connection;
        	}

        public void run() {
        	String username = null;
        	try{
			//initialize Input and Output streams
        		
        		out = new DataOutputStream(connection.getOutputStream());
        		out.flush();
        		in = new DataInputStream(connection.getInputStream());
        		username = in.readUTF();
        		int a = 0;
        		if(user.size()==0){
        			out.writeUTF("success");
        			socket1.add(connection);user.add(username);
        			System.out.println("Client "+ username+ " is connected");}
        		else{
        		for(int i=0; i<user.size();i++)
        		{
        			if((user.get(i)).contentEquals(username)){
        				String msg = "failure";
        				a = 1;
        				out.writeUTF(msg);
        				
        				
        			}
        			
        		}
        		if(a==0){
        			
        			out.writeUTF("success");
        			socket1.add(connection);
        			user.add(username);
        			System.out.println("Client "+ username+ " is connected");
        		}}
				
				
				
				while(true)
				{
					String filename = null; long filesize = 0; File temp = null;
					String sendtype = in.readUTF();
					String msgtype = in.readUTF();
					String clientname = null;
					if(!sendtype.contentEquals("broadcast")){
						clientname = in.readUTF();
					}
					String message = null;
					if(msgtype.contentEquals("message")){
						message = in.readUTF();
						
					}
					
					else if(msgtype.contentEquals("file")){
						filename = in.readUTF();
						filesize = in.readLong();
						temp = new File(System.getProperty("java.io.tmpdir"), filename);
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
	                    byte[] buffer = new byte[1024];
	                    long temp1 = filesize;
	                    while(temp1 > 0) {
	                        int size = in.read(buffer);
	                        bos.write(buffer, 0, size);
	                        temp1 = temp1 - size;
	                    }
	                    bos.flush();
	                    bos.close();
						
					}
					if(sendtype.contentEquals("broadcast")){
						for(String name : user){
							if(!name.contentEquals(username)){
								DataOutputStream out = new DataOutputStream(socket1.get(user.indexOf(name)).getOutputStream());
								sendMessage(out,username,msgtype,message,temp, filesize);
							}
						}
						if(msgtype.contentEquals("message")){
	                        System.out.println(username +" broadcasted message");}
	                        else{
	                        	System.out.println(username +" broadcasted file");
	                        }
					}
					else if(sendtype.contentEquals("unicast")){
						DataOutputStream out = new DataOutputStream(socket1.get(user.indexOf(clientname)).getOutputStream());
						sendMessage(out,username,msgtype,message,temp, filesize);
						if(msgtype.contentEquals("message")){
                            System.out.println(username +" unicast message to "+clientname);}
                            else{
                            	System.out.println(username +" unicast file to "+clientname);
                            }
					}
					else if(sendtype.contentEquals("blockcast")){
						for(String name : user){
							if(!name.contentEquals(username) && !name.contentEquals(clientname)){
								DataOutputStream out = new DataOutputStream(socket1.get(user.indexOf(name)).getOutputStream());
								sendMessage(out,username,msgtype,message,temp, filesize);
							}}
							if(msgtype.contentEquals("message")){
	                            System.out.println(username +" blockcast message excluding "+clientname);}
	                            else{
	                            	System.out.println(username +" blockcast file excluding "+clientname);
	                            }
					
					
				
				
}if(temp != null && temp.exists())
    temp.delete();
		}
        	}
        	catch(IOException ioException){
        		if(username != null){
                    
                    int a = username.indexOf(username);
                    socket1.remove(a);
                    user.remove(a);
                }
        		System.out.println("Disconnect with Client " + username);
		}
        	finally{
			//Close connections
        		try{
        			
        			in.close();
        			out.close();
        			connection.close();
        		}	
        		catch(IOException ioException){
        			System.out.println("Disconnect with Client " + username);
			}
		}
	}

	//send a message to the output stream
	public void sendMessage(DataOutputStream out, String sendingclient, String msgtype, String message, File temp, long filesize)
	{
		try {
			out.writeUTF(sendingclient);
			out.writeUTF(msgtype);
			if(msgtype.contentEquals("message")){
				out.writeUTF(message);
				}
			else if(msgtype.contentEquals("file")) {
	            out.writeUTF(temp.getName());
	            out.writeLong(filesize);
	            BufferedInputStream bos = new BufferedInputStream(new FileInputStream(temp));
	            byte[] buffer = new byte[1024];
	            while(filesize > 0) {
	                int size = bos.read(buffer);
	                out.write(buffer, 0, size);
	                filesize = filesize - size;
	            }
	            bos.close();
	        }
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		

    }

    	}

