import java.util.Scanner;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {

	private static int PORT = 9090;
	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;
	static Scanner scanner = new Scanner(System.in);
	private static PrihvatiPorukeServera pr;
	

	public static void main(String[] args) throws Exception {
		String serverAdress = "localhost";
		
		try {
			socket = new Socket(serverAdress,PORT);
			//PrihvatiPorukeServera pr = new PrihvatiPorukeServera(socket);
			//pr.start();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out = new PrintWriter(socket.getOutputStream(),true);
			System.out.println("Unesite ime: ");
			out.println(scanner.nextLine());
			System.out.println("Unesite ime kome saljete: ");
			out.println(scanner.nextLine());
			
			System.out.println("Konekcija osposobljena");
			pr = new PrihvatiPorukeServera(socket);
			pr.start();
			while(true) {
				out.println(scanner.nextLine());
				//System.out.println("Odgovor servera: " + in.readLine());
			}
			
			//out.println("Poruka za server");
			
			/*while(true) {
				System.out.println("Odgovor servera: " + in.readLine());
			}*/
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			socket.close();
		}
		
		//socket = new Socket(serverAdress,PORT);
		
	}
}


class PrihvatiPorukeServera extends Thread{
	
	private BufferedReader in;
	Socket soc;
	
	PrihvatiPorukeServera(Socket soc){
		try {
			this.soc = soc;
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
				System.out.println(in.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}