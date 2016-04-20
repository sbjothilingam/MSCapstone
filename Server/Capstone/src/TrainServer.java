import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TrainServer {
	
	static String mobileIp;
	static ServerSocket serverSocket ;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TrainServer server = new TrainServer();
		try{
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			serverSocket = new ServerSocket(6666);
			Socket socket = serverSocket.accept();
			
			BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message = read.readLine();
			if(message.equals("Hello")){
				mobileIp = socket.getInetAddress().getHostAddress();
				System.out.println("Connected Mobile IP "+mobileIp);
			}
			read.close();
			socket.close();
			
			
			socket = new Socket(mobileIp, 5555);
			PrintWriter write = new PrintWriter(socket.getOutputStream(), true);
			Scanner scan = new Scanner(new InputStreamReader(System.in));
			while(true){
				message = scan.nextLine();
				if(message.equals("activity")){
					write.println(message);
					message = scan.nextLine();
					write.println(message);
				} else{
					write.println(message);
				}
			}
			//write.println("activity");
			//write.println("walking");
			//write.close();
			//socket.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
