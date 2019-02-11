import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {

	private static int PORT = 9090;
	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;
	static Scanner scanner = new Scanner(System.in);
	private static PrihvatiPorukeServera pr;

	public static void main(String[] args) {
		String serverAdress = "localhost";
		
		
		
		try {
			socket = new Socket(serverAdress,PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out = new PrintWriter(socket.getOutputStream(),true);
			
			
			
			System.out.println("Prijavljujte se kao: (N/P) N-Novi, P-Postojeci");
			out.println(scanner.nextLine());
			
			
			
			while(true) {
				System.out.println("Unesite ime: ");
				out.println(scanner.nextLine());
				String n = in.readLine();
				if(n.equals("SUC")) {
					break;
				}else if(n.equals("FAILOSOBA")){
					System.out.println("Osoba vec postoji");	
				}else if(n.equals("OSOBAFAIL")) {
					System.out.println("Osoba ne postoji");
				}
			}
			while(true) {
				System.out.println("Unesite kome saljete: ");
				out.println(scanner.nextLine());
				if(in.readLine().equals("SUC")) {
					break;
				}else {
					System.out.println("Korisnik ne postoji, probajte s drugim imenom");
				}
			}
			
			
			System.out.println("Konekcija osposobljena");
			pr = new PrihvatiPorukeServera(socket);
			pr.start();
			while(true) {
				out.println(scanner.nextLine());
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
			socket.close();
			}catch(Exception e) {}
		}
		
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