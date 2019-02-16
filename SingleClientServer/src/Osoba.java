import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Osoba extends Thread{
		
		Socket soc;
		BufferedReader in;
		PrintWriter out;
		Connection con;
		
		private String name;
		private String toName;
		private int nameID;
		
		private static Set<String> threads = new HashSet<String>();
		private static Map<String,PrintWriter> maps = new HashMap<String,PrintWriter>();
		
		public Osoba(Socket soc) {
			this.soc = soc;
			try {
				in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
				out = new PrintWriter(soc.getOutputStream(),true);
				con = makeConnection();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			try {
				//Prilikom indentificiranja osobe pomoæu imena i lozinke(uskoro)
				 while(true) {
					name = in.readLine();
					 if(isThere(name) && !isOnline(name)) {
						 threads.add(name);
						 out.println("SUC1");
						 break;
					 }else {
						 if(isOnline(name)) {
							 out.println("FAIL");
						}else {
							addUser(name);
							out.println("SUC2");
							break;
						}
						
					 }
					
				 }
				
				while(true) {
					toName = in.readLine();
					if(isThere(toName) && !toName.equals(name)) {
						out.println("SUC3");
						break;
					}else {
						out.println("FAIL1");
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
					if(s.length()<=0) {
						out.println("Poruka ne smije biti prazna");
						continue;
					}
					if(isOnline(toName)) {
						out = maps.get(toName);
						out.println("[" + name + "] " + s);
					}

					SaveMessage(s,name);
					SaveMessage(s,toName);	
				}			
			}catch(Exception e) {
				e.printStackTrace();
			}finally{
				if(maps.get(name)!= null)
					maps.remove(name);
				if(threads.contains(name)) {
					threads.remove(name);
				}
				try{soc.close();
				}catch(Exception e) {
					e.printStackTrace();
					}
			}
		}

		private void LoadMessage(PrintWriter pw) {
			try (Statement stmt = con.createStatement()){
				ResultSet rs = stmt.executeQuery("Select * from Poruke WHERE KorisnikID = " + nameID);
				while(rs.next()) {
					String from = rs.getString(6);
					pw.println("[" + (from.equals(this.name) ? "ME" : from.toUpperCase()) + "] " + rs.getString(3));
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}				
		}
		
		private void SaveMessage(String message, String name) {

			String query="INSERT INTO Poruke (KorisnikID,Poruka,Primatelj,Datum,Posiljatelj) VALUES (?,?,?,?,?)";

			try (PreparedStatement pStmt = con.prepareStatement(query)){
				pStmt.setInt(1, LoadID(name));
				pStmt.setString(2, message);
				pStmt.setString(3, this.toName);
				pStmt.setObject(4, LocalDate.now(ZoneId.systemDefault()));
				pStmt.setObject(5, this.name);
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
		
		private boolean isOnline(String name) {
			return threads.contains(name);
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
