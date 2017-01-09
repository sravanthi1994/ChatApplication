

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Client {
	
	Socket requestSocket;
	DataOutputStream out;
	DataInputStream in;
	private String username;
	
	


	public Client(String username, int portnumber) {
		
		try {
			this.username = username;
			requestSocket = new Socket("localhost", portnumber);
			
			out = new DataOutputStream(requestSocket.getOutputStream());
			in = new DataInputStream(requestSocket.getInputStream());
			out.writeUTF(username);
			out.flush();
			String error = null;
			error = in.readUTF();
			if(error.contentEquals("failure")){System.out.println("Username already exists - Please use a different username"); System.exit(1);}
System.out.println("Client is connected to server");
		
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
	private class getMessages implements Runnable {
		public void run(){
			try{
				while(true){
					String fromclient = null;			
					fromclient = in.readUTF();
					String msgtype = in.readUTF();
					if(msgtype.contentEquals("message")){
						String message = in.readUTF();
						System.out.println(fromclient + " : "+ message);}
					else if(msgtype.contentEquals("file")) {
                        String fileName = in.readUTF();
                        long fileSize = in.readLong();
                        String directoryName = System.getProperty("user.dir") + File.separator + username;
                        File dir = new File(directoryName);
                        if(!dir.exists()) {
                            dir.mkdir();
                        }
                        File file = new File(dir.getAbsolutePath(), fileName);
                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()));
                        byte[] buffer = new byte[1024];
                        while(fileSize > 0) {
                            int size = in.read(buffer);
                            outputStream.write(buffer, 0, size);
                            fileSize = fileSize - size;

                        }
                        outputStream.flush();
                        outputStream.close();
                        System.out.println(fromclient + ": File got saved to " + file.getAbsolutePath());
                    }
				}
			}catch (IOException e) {
            	System.out.println("Server not reachable");
            }
		}
					
						
					
			
			
		
	}
	
	private void sendMessage(){
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			System.out.println("Please enter the command as a string");
			try {
				String command = bufferedReader.readLine();
				String[] splitted = command.split(" ");
				if(splitted.length < 3){
					System.out.println("Invalid command");
					continue;
				}
				String sendtype = splitted[0];
				String msgtype = splitted[1];
				String message = null;
				Pattern p = Pattern.compile(".*\\\"(.*)\\\".*");
                Matcher m = p.matcher(command);
                if (m.find()) {
                    message = m.group(1);
                    if (message.length() == 0) {
                        System.out.println("Message/Filename is empty");
                        continue;
                    }
                } else {
                    System.out.println("Please include the message/filename in quotes");
                    continue;
                }
                
                String clientname = null;
                if(!sendtype.contentEquals("broadcast")){
                	clientname = splitted[splitted.length-1];
                }
                out.writeUTF(sendtype);
                out.writeUTF(msgtype);
                if(clientname != null){
                	out.writeUTF(clientname);
                }
                if(msgtype.contentEquals("message")){
                	out.writeUTF(message);
                	out.flush();
                	System.out.println("Message sent successfully!");
                }
                else if(msgtype.contentEquals("file")){
                	File file = new File(message);
                	if(!file.exists()){
                		System.out.println(message+ " file is not found - Please enter a file name with its absolute path");
                		continue;
                	}
                	out.writeUTF(file.getName());
                	out.writeLong(file.length());
                	BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                	int packetsize=1024;
                	double nosofpackets=Math.ceil(((int) file.length())/packetsize);
                	for(double i=0; i<nosofpackets+1; i++){
                		byte[] buffer = new byte[1024];
                		bis.read(buffer,0,buffer.length);
                		out.write(buffer,0,buffer.length);
                		out.flush();
                	}
                	
                	bis.close();
                	System.out.println("File sent successfully!");
                	
                }
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
	}
		
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length < 2)
		{System.out.println("Please enter both first argument as username and secong argument as port number ");
		System.exit(1);
		}
		Client client = new Client(args[0],Integer.parseInt(args[1]));
		getMessages gm = client.new getMessages();
		Thread t = new Thread(gm);
        t.start();
		client.sendMessage();
		
		
	}

}
