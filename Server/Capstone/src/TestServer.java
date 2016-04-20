import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/*
 * @author : Suresh Babu Jothilingam
 */
public class TestServer {

	static ServerSocket serverSocket;

	class ServerThread extends Thread{
		private Socket socket;
		private ExtractFeatureAndMergeMethods extract;
		private ClassifyInstance classify;
		private Socket sendSocket;
		private ObjectOutputStream sendActivity;
		
		public ServerThread(Socket socket, String mobileIp) throws Exception {
			// TODO Auto-generated constructor stub
			this.socket = socket;
			this.extract = new ExtractFeatureAndMergeMethods();
			this.classify = new ClassifyInstance(FilePaths.rootPathModels);
			this.sendSocket = new Socket(mobileIp, 5555);
			this.sendActivity = new ObjectOutputStream(this.sendSocket.getOutputStream());
		}
		@Override
		public void run() {
			try{
				ObjectInputStream read = new ObjectInputStream(this.socket.getInputStream());
				while(true){
					String message = read.readObject().toString();
					try{
					//receive collected object extract feature and send it to the Model for classification
					if(message.equals("data")){
						ArrayList<String> list = new ArrayList<String>();
						String activity = "";
						
						ArrayList<String> data = (ArrayList<String>) read.readObject();
						System.out.println("*************************************************");
						System.out.println("Recived unknown instance "+data.size()+" "+data);

						double[] instance = this.extract.getAccelerationMagnitude(data);
						this.classify.setValues(this.extract.getFundamentalFrequency(instance), this.extract.getAvgAcceleration(instance), this.extract.getMinAmplitude(instance), this.extract.getMaxAmplitude(instance));
						
						String j48Result = this.classify.j48Result(), naiveBayesResult = this.classify.naiveBayesResult();
						String randomForestResult = this.classify.randomForestResult(), kNNResult = this.classify.kNNResult();
						
						list.add(j48Result); list.add(naiveBayesResult); list.add(kNNResult); list.add(randomForestResult);
						
						System.out.println("J48 Result "+j48Result);
						System.out.println("Naive Bayes Result "+naiveBayesResult);
						System.out.println("Random Forest Result "+randomForestResult);
						System.out.println("KNN Result "+kNNResult);
						System.out.println("*************************************************");
						
						sendResult(returnActivity(list));
					} else if(message.equals("stop")){
						sendResult("stop");
						break;
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				read.close();
				this.socket.close();
				this.sendActivity.close();
				this.sendSocket.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		String returnActivity(ArrayList<String> list){
			String activity = "";
			int sitting = 0, walking = 0, standing = 0;
			for(int i=0;i<list.size();i++){
				switch(list.get(i)){
				case "sitting":
					sitting++;
					break;
				case "walking":
					walking++;
					break;
				case "standing":
					standing++;
					break;
				}
			}
			if(sitting>walking && sitting>standing){
				activity = "sitting";
			} else if(walking>sitting && walking>standing){
				activity = "walking";
			} else if(standing>walking && standing>sitting){
				activity = "standing";
			} else{
				activity = "none";
			}
			
			return activity;
			
		}
		
		void sendResult(String activity){
			try {
				sendActivity.writeObject(activity);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class Server extends Thread{
		String mobileIp;
		public Server(String mobileIp) {
			// TODO Auto-generated constructor stub
			this.mobileIp = mobileIp;
		}
		@Override
		public void run() {

			// TODO Auto-generated method stub
			super.run();
			try{
				System.out.println("Server Started");
				while(true){
					new ServerThread(serverSocket.accept(), this.mobileIp).start();
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}

	}
	class HelloServer extends Thread{
		@Override
		public void run() {

			// TODO Auto-generated method stub
			super.run();
			try{
				System.out.println("Hello Server Started");
				ServerSocket helloServer =  new ServerSocket(5566);
				while(true){
					Socket socket = helloServer.accept();
					ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
					String message = read.readObject().toString();
					if(message.equals("Hello")){
						System.out.println("Connected Mobile IP "+socket.getInetAddress().getHostAddress());
						new Server(socket.getInetAddress().getHostAddress()).start();
					}
					read.close();
					socket.close();
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			TestServer server = new TestServer();

			serverSocket = new ServerSocket(6666);
			/*
			Socket socket = serverSocket.accept();
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			String message = read.readObject().toString();
			if(message.equals("Hello")){
				mobileIp = socket.getInetAddress().getHostAddress();
				System.out.println("Connected Mobile IP "+mobileIp);
			}
			read.close();
			socket.close();

			server.new Server().start();
			*/
			server.new HelloServer().start();
		}catch(Exception e){
			e.printStackTrace();
		}

		//server.new Server().start();

	}

}
