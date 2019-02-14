import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.time.ZoneId;
import java.util.Date;



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
		private int nameID;
		

		
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
					if(login.toUpperCase().equals("P")) {
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
				nameID = LoadID(name);
				
				out.println("Ime prihvaceno");
				LoadMessage(maps.get(name));
				
				while(true) {
					String s = in.readLine();
					if(s.length()<=0) continue;
						
					out = maps.get(toName);
					out.println("[" + name + "] " + s);

					SaveMessage(s,name,toName);
					SaveMessage(s,toName,name);
					
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
		
		private void LoadMessage(PrintWriter pw) {
			try (Statement stmt = con.createStatement()){
				ResultSet rs = stmt.executeQuery("Select * from Poruke WHERE KorisnikID = " + nameID + " ORDER BY Datum DESC");
				while(rs.next()) {
					String from = rs.getString(4);
					pw.println("[" + (from.equals(name) ? "JA" : from.toUpperCase()) + "] " + rs.getString(3));
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}	
					
		}
		
		private void SaveMessage(String message, String name, String toName) {
			Date date = new Date();
			String query="INSERT INTO Poruke (KorisnikID,Poruka,Posiljatelj,Datum) VALUES (?,?,?,?)";

			try (PreparedStatement pStmt = con.prepareStatement(query)){

				pStmt.setInt(1, LoadID(name));
				pStmt.setString(2, message);
				pStmt.setString(3, toName);
				pStmt.setObject(4, date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				pStmt.execute();
				
			}catch(SQLException e) { 
				e.printStackTrace(); 
			}
			
		}
		
		private int LoadID(String name) {
			int id=0;
			try(Statement pStmt = con.createStatement()){
				ResultSet rs = pStmt.executeQuery("Select ID from Korisnici WHERE Korisnik = '" + name +"'");
				rs.next();
				id = rs.getInt(1);		
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
			return id;
			
		}
		
		private Connection makeConnection() throws Exception {
				Class.forName("com.mysql.jdbc.Driver");
				String connectionUrl = "jdbc:sqlserver://localhost;database=ClientServer;user=jure;password=jure;";
				Connection con = DriverManager.getConnection(connectionUrl);
				return con;
				
			}
		

		private boolean isThere(String name) {
			try (Statement stmt = con.createStatement()){
				
				ResultSet rs = stmt.executeQuery("SELECT Korisnik FROM Korisnici where Korisnik = '" + name + "'");
				if(rs.next())
					return true;
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			return false;
				
		}
		
		
		private void addUser(String name) {
			try(PreparedStatement pstmt = con.prepareStatement("insert into Korisnici (Korisnik)" + " values (?)")){
				pstmt.setString (1, name);
				pstmt.execute();
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
}
	



