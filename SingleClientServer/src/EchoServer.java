import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;




public class EchoServer {
	private static int PORT = 9090;
	public static Set<String> names = new HashSet<>();
	private static Set<PrintWriter> writers = new HashSet<>();
	private static ServerSocket server;
	private static Map<String,PrintWriter> maps = new HashMap<String,PrintWriter>();
	
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("Server is running");
		try {
			server = new ServerSocket(PORT);
			while(true) {
				new Osoba(server.accept()).start();
			}
		}catch(Exception e) {
			
		}finally {
			server.close();
		}
		
		
	}
	
	
	private static class Osoba extends Thread{
		
		Socket soc;
		BufferedReader in;
		PrintWriter out;
		
		private String name;
		private String toName;

		
		public Osoba(Socket soc) {
			this.soc = soc;
			try {
				in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
				out = new PrintWriter(soc.getOutputStream(),true);
				
			}catch(Exception e) {
				
			}
		}
		
		public void run() {
			try {
				name = in.readLine();
				toName = in.readLine();
				synchronized(maps) {
					if(!maps.containsKey("name")) {
						maps.put(name, out);
					}
				}
				out.println("Ime prihvaceno");
				//writers.add(out);
				
				
				while(true) {
					String s = in.readLine();
					
					PrintWriter wout = maps.get(toName);
					wout.println("[" + name + "] " + s);
					/*for(PrintWriter pw : writers) {
						pw.println("Poruka od servera za sve" + s );
					}*/
					//out.println(s);
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}finally{
				if(maps.get(name)!= null)
					maps.remove(name);
				
				if(names != null) {
					names.remove(name);
				}
				if(out != null) {
					writers.remove(out);
				}
				try{soc.close();
				}catch(Exception e) {
					e.printStackTrace();
					}
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}



