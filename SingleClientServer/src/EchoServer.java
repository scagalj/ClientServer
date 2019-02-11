import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;




public class EchoServer {
	private static int PORT = 9090;
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
		Connection con;
		
		private String name;
		private String toName;
		

		
		public Osoba(Socket soc) {
			this.soc = soc;
			try {
				in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
				out = new PrintWriter(soc.getOutputStream(),true);
				con = makeConnection();
			}catch(Exception e) {
				
			}
		}
		
		public void run() {
			try {
				String login;
				login = in.readLine();
				while(true) {
					name = in.readLine();
					if(login.equals("P")) {
						if(isThere(name) == true) {
							out.println("SUC");
							break;
						}else {
							out.println("OSOBAFAIL");
						}
					}else {
						if(isThere(name) == false) {
							addUser(name);
							out.println("SUC");
							break;
						}else {
							out.println("FAILOSOBA");
						}
						
					}
			
				}

				while(true) {
					toName = in.readLine();
					if(isThere(toName) == true) {
						out.println("SUC");
						break;
					}else {
						out.println("FAIL");
					}
					
				}
				
				
				synchronized(maps) {
					if(!maps.containsKey("name")) {
						maps.put(name, out);
					}
				}
				out.println("Ime prihvaceno");
				
				
				while(true) {
					String s = in.readLine();
					
					PrintWriter wout = maps.get(toName);
					wout.println("[" + name + "] " + s);
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}finally{
				if(maps.get(name)!= null)
					maps.remove(name);
				
				try{soc.close();
				}catch(Exception e) {
					e.printStackTrace();
					}
			}
		}
		
		private Connection makeConnection() throws Exception {
				Class.forName("com.mysql.jdbc.Driver");
				String connectionUrl = "jdbc:sqlserver://localhost;database=ClientServer;user=jure;password=jure;";
				Connection con = DriverManager.getConnection(connectionUrl);
				return con;
				
			}
		

		private boolean isThere(String name) throws Exception {
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Korisnik FROM Korisnici");
			while(rs.next()) {
				if(rs.getString(1).equals(name)) {
					return true;
				}
			}
			return false;
		}
		
		
		private void addUser(String name) throws Exception {
			PreparedStatement pstmt = null;
			try{
				pstmt = con.prepareStatement("insert into Korisnici (Korisnik)" + " values (?)");
				pstmt.setString (1, name);
				pstmt.execute();
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if(pstmt != null) {
					pstmt.close();
				}
			}
		}
	}
}
	



