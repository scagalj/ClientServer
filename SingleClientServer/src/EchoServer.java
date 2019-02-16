import java.net.ServerSocket;


public class EchoServer {
	private static int PORT = 9090;
	
	
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("Server is running");
		try (ServerSocket server = new ServerSocket(PORT)){
			while(true) {
				new Osoba(server.accept()).start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
	



