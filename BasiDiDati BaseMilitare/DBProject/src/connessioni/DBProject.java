package connessioni;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class DBProject {

	public DBProject() {
	}

	public void menu() {
		InputStreamReader keyIS;
		BufferedReader keyBR;
		int i = 0;
		String scelta;

		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);

		while (i != 1000) {
			System.out.println("\nSeleziona operazione:");
			System.out.println("1, Inserisci un nuovo Militare");
			System.out.println("2, Aggiungi un'arma ad un soldato");
			System.out.println("3, Aggiungere un Veicolo ad un Pilota");
			System.out.println("4, Trovare il n° oggetti in Deposito di ogni Caserma");
			System.out.println("5, Aggiungi un Militare ad una Guarnigione");
			System.out.println("6, Trovare il n° di subordinati guidati da un Ufficiale ");
			System.out.println("7, Inserire una Guarnigione ad una Battaglia ");
			System.out.println("8, Aggiungere una Battaglia ");
			System.out.println("9, Inserire un nuovo Veicolo al Deposito ");
			System.out.println("10,Inserire una nuova Arma al Deposito ");
			System.out.println("11,Inserire una nuova Guanigione ");
			System.out.println("1000, Per uscire");

			System.out.print("Inserisci scelta: ");
			try {
				scelta = keyBR.readLine();

				try {
					i = Integer.parseInt(scelta);
				} catch (NumberFormatException e) {
					i = 999;
				} 

				switch (i) {
				
				case 1: {
					System.out.println("Inserisci il nome del Militare:");
					String nomeMilitare = keyBR.readLine();
					System.out.println("Inserisci il cognome del Militare:");
					String cognomeMilitare = keyBR.readLine();
					System.out.println("Inserisci l'eta del Militare:");
					int etaMilitare = Integer.parseInt(keyBR.readLine());
					selectWhatUWant("caserma");
					System.out.println("Inserisci la Caserma in cui inserire il Militare(CodiceAlfanumerico)=:");
					String casermaMilitare = keyBR.readLine();
					Random rnd = new Random();
					int matricola = rnd.nextInt(99999999);
					insertMilitare(matricola, nomeMilitare, cognomeMilitare, etaMilitare, casermaMilitare);


					break;
				}
				case 2: {
					selectWhatUWant("arma");
					selectWhatUWant("soldato");
					selectWhatUWant("equipaggia");
					System.out.println("Inserisci la matricola di un Soldato ");
					int matricolaSoldato = Integer.parseInt(keyBR.readLine());
					System.out.println("Inserisci il numero di serie di un'Arma");
					int serieArma = Integer.parseInt(keyBR.readLine());
					System.out.println("Inserisci data odierna(YYYYMMDD) ");
					Date data = new Date(Long.parseLong(keyBR.readLine()));
					updateArmaToSoldier(data, matricolaSoldato, serieArma);
					break;
				} 
				case 3: {
					updateVeicoloToPilota();
					break;
				}
				case 4: {
					selectDeposito();
					break;
				}
				case 5: {
					updateMilitareToG();
					break;
				}
				case 6: {
					selectUfficiali();
					break;
				}
				case 7: {
					insertGuarnigioneToB();
					break;
				}
				case 8: {
					insertBattaglia();
					break;
				}
				case 9: {
					insertVeicoloToD();
					break;
				}
				case 10: {
					insertArmaToDeposit();
					break;
				}
				case 11: {
					insertGuarnigione();
				}
				case 99: {
					String x = keyBR.readLine();
					selectTabella(x);
				}
				case 1000: {
					System.out.println("Uscita");
					break;
				}
				default: {
					System.out.println("Scelta non presente");
					break;
				}
				}
			} catch (IOException e) {
			} 
		}
	} 
	
	

	/*Query 1*/
	private void insertMilitare(int matricola, String nome, String cognome, int eta,String alfa) {
		Connection con = null;
		Statement st = null;
		
		InputStreamReader keyIS;
		BufferedReader keyBR;
		
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
		
		String qualificaVeicolo = null;
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet uprs = st.executeQuery("SELECT * FROM militare");
			uprs.moveToInsertRow();
			uprs.updateInt(1, matricola);
			uprs.updateString(2, nome);
			uprs.updateString(3, cognome);
			uprs.updateInt(4, eta);
			uprs.updateString(5, alfa);
			uprs.insertRow();
			System.out.println("Inserire il ruolo del militare(usa: Pilota, Ufficiale o Soldato)");
			String ruoloMilitare = keyBR.readLine();
			if(ruoloMilitare.equals("Pilota")) {
				int i = 0;
				while(i == 0) {
					System.out.println("Inserire la qualifica del pilota(usa: Caccia, Sottomarino o CarroArmato)");
					qualificaVeicolo = keyBR.readLine();
					if(qualificaVeicolo.equals("Caccia") || qualificaVeicolo.equals("Sottomarino") || qualificaVeicolo.equals("CarroArmato"))
						i = 1;
				}
				int n = st.executeUpdate("INSERT INTO " + ruoloMilitare + " VALUES ('" + qualificaVeicolo + "'," + matricola + ");");
				if(n > 0) {
				}
				else {
					System.out.println("Impossibile inserire il record");
				}
			}
			else if(ruoloMilitare.equals("Ufficiale")) {
				System.out.println("Inserire il grado dell'ufficiale");
				int grado = Integer.parseInt(keyBR.readLine());
				int n = st.executeUpdate("INSERT INTO " + ruoloMilitare + " VALUES (" + grado + ", 0, " + matricola + ");");
				if(n > 0) {
				}
				else {
					System.out.println("Impossibile inserire il record");
				}	
			}
			else {
				System.out.println("Inserire il ruolo del soldato");
				String ruoloSoldato = keyBR.readLine();
				int n = st.executeUpdate("INSERT INTO " + ruoloMilitare + " VALUES ('" + ruoloSoldato + "', " + matricola + ");");
				if(n > 0) {
				}
				else {
					System.out.println("Impossibile inserire il record");
				}
			}
			System.out.println("Query OK!");
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
	}
	
	/*Query 2*/
	private void updateArmaToSoldier(Date data, int matricola, int nSerie) {

		Connection con = null;
		Statement st = null;

		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet uprs = st.executeQuery("SELECT * FROM equipaggia");
			uprs.moveToInsertRow();
			uprs.updateDate("dataAssegnamentoArma", data);
			uprs.updateInt("matricolaSoldato", matricola);
			uprs.updateInt("numeroserieArma", nSerie);
			uprs.insertRow();
			System.out.println("Query OK!");
		} catch (SQLException e) {
			printSQLException(e);
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
	}
	
	/*Query 3*/
	private void updateVeicoloToPilota() {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		InputStreamReader keyIS;
		BufferedReader keyBR;
		
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
		

		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			System.out.println("Inserisci la qualifica di un Pilota tra: Caccia/CarroArmato/Sottomarino ");
			String qualificaVeicolo = keyBR.readLine();
			String sql = "select nome, militare.matricola from pilota, militare where pilota.matricola = militare.matricola && pilota.qualificaTipoVeicolo = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, qualificaVeicolo);
			rs = ps.executeQuery();
			printResult(rs);
			
			rs = st.executeQuery("SELECT * FROM veicolo, "+ qualificaVeicolo +" WHERE veicolo.matricola= " + qualificaVeicolo + ".matricola");
			printResult(rs);
			
			System.out.println("Inserisci la matricola di un Pilota ");
			int matricolaPilota =Integer.parseInt(keyBR.readLine());
			
			System.out.println("Inserisci la matricola di Veicolo");
			String matricolaVeicolo = keyBR.readLine();
			
			ResultSet uprs = st.executeQuery("SELECT * FROM guida");
			uprs.moveToInsertRow();
			uprs.updateInt(1, matricolaPilota);
			uprs.updateString(2, matricolaVeicolo);
			uprs.insertRow();	
			System.out.println("Query OK!");
			
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
	}

	/*Query 4*/
	private void selectDeposito() {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT sum(nOggettiInDeposito), codiceAlfanumerico FROM Caserma group by codiceAlfanumerico;");
			printResult(rs);
			System.out.println("Query OK!");
		} catch (SQLException e) {
			printSQLException(e);
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
	}
	
	/*Query 5*/
	private void updateMilitareToG() {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		InputStreamReader keyIS;
		BufferedReader keyBR;
		
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
		

		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement();
			
			rs = st.executeQuery("SELECT * FROM Militare WHERE NOT EXISTS (SELECT * FROM Partecipa WHERE militare.matricola = partecipa.matricolaMilitare);");
			printResult(rs);
			selectWhatUWant("guarnigione");
			System.out.println("Inserisci la matricola di un Militare ");
			int matricolaMilitare =Integer.parseInt(keyBR.readLine());
			
			System.out.println("Inserisci il codice di una Guarnigione");
			int codiceGuarnigione =Integer.parseInt(keyBR.readLine());
			
			int n = st.executeUpdate("INSERT INTO partecipa " + "VALUES(" + matricolaMilitare + ", " + codiceGuarnigione + ");");
			if (n > 0) {
				System.out.println("Query OK!");
			} else {
				System.out.println("Impossibile inserire il record");
			}
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
		
	}
	
	/*Query 6*/
	private void selectUfficiali() {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		InputStreamReader keyIS;
		BufferedReader keyBR;
		
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
		
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("Select nome, cognome, matricola From ufficiale Natural Join militare Where ufficiale.matricola = militare.matricola");
			printResult(rs);
			System.out.println("Inserisci la matricola dell'Ufficiale da visualizzare");
			int matricolaufficiale = Integer.parseInt(keyBR.readLine());
			rs = st.executeQuery("SELECT nSubordinati FROM ufficiale WHERE ufficiale.matricola = " + matricolaufficiale + ";");
			printResult(rs);
			System.out.println("Query OK!");
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
		
	}
	
	/*Query 7*/
	private void insertGuarnigioneToB() {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		InputStreamReader keyIS;
		BufferedReader keyBR;
		
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
		

		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement();
			
			rs = st.executeQuery("SELECT nome,annoInizio From battaglia Where battaglia.tipo = 'incorso'");
			printResult(rs);
			
			System.out.println("Inserisci il nome di una battaglia ");
			String nomebattaglia = keyBR.readLine();
			
			System.out.println("Inserisci l'anno di inizio della battaglia");
			int annobattaglia =Integer.parseInt(keyBR.readLine());
			
			rs = st.executeQuery("SELECT * FROM guarnigione;");
			printResult(rs);
			
			System.out.println("Inserisci il codice della guarnigione");
			int codiceGuarnigione =Integer.parseInt(keyBR.readLine());
			
			int n = st.executeUpdate("UPDATE guarnigione,battaglia SET guarnigione.nomeBattaglia = '" + nomebattaglia + "', annoBattaglia= "+ annobattaglia +" WHERE guarnigione.codice =" + codiceGuarnigione + ";");
			if (n > 0) {
				System.out.println("Query OK!");
			} else {
				System.out.println("Impossibile inserire il record");
			}
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
	}

	/*Query 8*/
	private void insertBattaglia() {
		Connection con = null;
		Statement st = null;
		
		boolean flag = true;
		
		InputStreamReader keyIS;
		BufferedReader keyBR;
		String tipobattaglia = null;
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
		
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement();
			System.out.println("Inserisci il nome battaglia");
			String nomebattaglia = keyBR.readLine();
			System.out.println("Inserisci l 'anno della battaglia:");
			int annobattaglia = Integer.parseInt(keyBR.readLine());
			System.out.println("Informare se la battaglia 'incorso' o 'conclusa'");
			while(flag) {
				tipobattaglia = keyBR.readLine();
				if(tipobattaglia.equals("incorso") || tipobattaglia.equals("conclusa")) {
					flag = false;
				}
				else {
					System.out.println("Tipo errato usa: incorso o conclusa ");
				}
			}
			int n = st.executeUpdate("INSERT INTO battaglia " + "VALUES ('"+ nomebattaglia + "', " + annobattaglia + ", '" + tipobattaglia + "');");
			if (n > 0) {
				System.out.println("Query OK!");
			} 
			else {
				System.out.println("Impossibile inserire il record");
			}
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
		
	}
	
	/*Query 9*/
	private void insertVeicoloToD() {
		Connection con = null;
		Statement st = null;
		InputStreamReader keyIS;
		BufferedReader keyBR;
		ResultSet rs = null;
		
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
		int numeroposti = 0;
		
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			String tipoVeicolo = "";
			
			selectWhatUWant("caserma");
			System.out.println("Inserisci codiceAlfanumerico di una caserma");
			String codiceAlfa = keyBR.readLine();
			rs = st.executeQuery("Select nDeposito from deposito where deposito.codiceAlfanumerico = '" + codiceAlfa +"'");
			printResult(rs);
			System.out.println("Inserisci il numero del deposito");
			int nDeposito = Integer.parseInt(keyBR.readLine());
			while(numeroposti == 0) {
				System.out.println("Inserire il tipo di Veicolo(usa: Sottomarino, CarroArmato o Caccia)");
				tipoVeicolo = keyBR.readLine();
				numeroposti = nPosti(tipoVeicolo);
			}
			System.out.println("Inserisci una matricola");
			String matricolaVeicolo = keyBR.readLine();
			System.out.println("Inserire anno di immatricolazione");
			int annoVeicolo = Integer.parseInt(keyBR.readLine());
			
			ResultSet uprs = st.executeQuery("SELECT * FROM veicolo");
			uprs.moveToInsertRow();
			uprs.updateString(4, codiceAlfa);
			uprs.updateString(3, matricolaVeicolo);
			uprs.updateInt(1, numeroposti);
			uprs.updateInt(2, annoVeicolo);
			uprs.updateInt(5, nDeposito);
			uprs.insertRow();	

			int n = st.executeUpdate(insertTipoVeicolo(tipoVeicolo, matricolaVeicolo));
			if(n > 0) {
				System.out.println("Query OK!");
			}
			else {
				System.out.println("Impossibile inserire il record");
			}
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}	
	}
	
	/*Query 10*/
	private void insertArmaToDeposit() {
	
	Connection con = null;
	Statement st = null;
	ResultSet rs = null;
	InputStreamReader keyIS;
	BufferedReader keyBR;
		
	keyIS = new InputStreamReader(System.in);
	keyBR = new BufferedReader(keyIS);
		
	try {
		con = DBConnectionPool.getConnection();
		st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		
		selectWhatUWant("caserma");
		
		System.out.println("Inserisci codiceAlfanumerico di una caserma");
		String codiceAlfa = keyBR.readLine();
		rs = st.executeQuery("Select nDeposito from deposito where deposito.codiceAlfanumerico = '" + codiceAlfa +"'");
		printResult(rs);
		System.out.println("Inserisci il numero del deposito");
		int nDeposito = Integer.parseInt(keyBR.readLine());
		System.out.println("Inserire il numero di Serie: ");
		int nSerie = Integer.parseInt(keyBR.readLine());
		System.out.println("Inserisci capacità massima proiettili:");
		int capProiettili = Integer.parseInt(keyBR.readLine());
		System.out.println("Inserire calibro Arma");
		double calArma = Double.parseDouble(keyBR.readLine());
			
		PreparedStatement ps = con.prepareStatement("INSERT INTO arma VALUES(?,?,?,?,?);");
		ps.setInt(1, nSerie);
		ps.setInt(2, capProiettili);
		ps.setDouble(3, calArma);
		ps.setString(4, codiceAlfa);
		ps.setInt(5, nDeposito);
		ps.executeUpdate();
		System.out.println("Query OK!");
	} catch (SQLException e) {
		printSQLException(e);
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if (st != null)
				st.close();
			DBConnectionPool.releaseConnection(con);
		} catch (SQLException e) {
			printSQLException(e);
		}
	}
	}
	
	/*Query 11*/
	private void insertGuarnigione() {

		Connection con = null;
		Statement st = null;
		
		InputStreamReader keyIS;
		BufferedReader keyBR;
			
		keyIS = new InputStreamReader(System.in);
		keyBR = new BufferedReader(keyIS);
			
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			System.out.println("Inserisci codice guarnigione");
			int codiceGuarnigione = Integer.parseInt(keyBR.readLine());
			
			System.out.println("Inserire stemma: ");
			String stemma = keyBR.readLine();
			
			selectWhatUWant("Ufficiale");
			
			System.out.println("Inserisci matricola ufficiale:");
			int matricolaUfficiale = Integer.parseInt(keyBR.readLine());
			
			selectWhatUWant("Battaglia");
			System.out.println("Inserire nome battaglia:");
			String nomeBattaglia = keyBR.readLine();
				
			System.out.println("Inserisci anno della battaglia:");
			int annoBattaglia = Integer.parseInt(keyBR.readLine());
			
			PreparedStatement ps = con.prepareStatement("INSERT INTO guarnigione VALUES(?,?,?,?,?)");
			ps.setInt(1, codiceGuarnigione);
			ps.setString(2, stemma);
			ps.setInt(3, matricolaUfficiale);
			ps.setString(4, nomeBattaglia);
			ps.setInt(5, annoBattaglia);
			ps.executeUpdate();
			System.out.println("Query OK!");
		} catch (SQLException e) {
			printSQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
	}
	
	/*Fissa la lunghezza delle stringhe*/
	private String fixedLengthString(String string) {
		int length = 20;
		return String.format("|%1$" + length + "s", string);
	}

	private void printResult(ResultSet rs) {
		try {
			rs.beforeFirst();
		} catch (SQLException e) {
			printSQLException(e);
		}
		printResultMeta(rs);
	}

	/*Stampa le tabelle*/
	private void printResultMeta(ResultSet rs) {
		try {
			System.out.println("");
			ResultSetMetaData rsmd = rs.getMetaData();

			String tableName = rsmd.getTableName(1);
			System.out.println("Tabella:" + tableName);

			int numberOfColumns = rsmd.getColumnCount();
			for (int i = 1; i <= numberOfColumns; i++) {
				String columnName = rsmd.getColumnLabel(i);
				System.out.print(fixedLengthString(columnName));
			}
			System.out.println("");
			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					String columnValue = rs.getString(i);
					System.out.print(fixedLengthString(columnValue));
				}
				System.out.println("");
			}
			System.out.println("\n");
		} catch (SQLException e) {
			printSQLException(e);
		}
	}
	
	/*Stampa degli errori*/
	public void printSQLException(SQLException ex) {
		System.out.println("SQLException description:");
		while (ex != null) {
			System.out.println("Message: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("ErrorCode: " + ex.getErrorCode());
			ex = ex.getNextException();
		}

	}
	
	/*Serve per inserire il tipo standard di veicolo*/
	private String insertTipoVeicolo(String tipoVeicolo, String matricola) {
		if(tipoVeicolo.equals("Sottomarino"))
			return "INSERT INTO " + tipoVeicolo + " VALUES (0 , 00000000, '" + matricola + "');";
		else if(tipoVeicolo.equals("Caccia"))
			return "INSERT INTO " + tipoVeicolo + " VALUES (0 , 0, '" + matricola + "');";
		else if(tipoVeicolo.equals("CarroArmato"))
			return "INSERT INTO " + tipoVeicolo + " VALUES (0 , 10.2, '" + matricola + "');";
		return null;
	}
	
	/*Numero di posti dei veicoli*/
	private int nPosti(String tipoVeicolo) {
		if(tipoVeicolo.equals("Sottomarino"))
			return 100;
		else if(tipoVeicolo.equals("Caccia"))
			return 2;
		else if(tipoVeicolo.equals("CarroArmato"))
			return 10;
		return 0;
	}

	/*Mostra qualsiasi tabella*/
	public void selectWhatUWant(String x) {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM " + x);
			printResult(rs);
		} catch (SQLException e) {
			printSQLException(e);
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}
	}
	
	private void selectTabella(String x) {
		Connection con = null;
		Statement st = null;
		
		try {
			con = DBConnectionPool.getConnection();
			st = con.createStatement();
			selectWhatUWant(x);
			System.out.println("Query OK!");
		} catch (SQLException e) {
			printSQLException(e);
		} finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} catch (SQLException e) {
				printSQLException(e);
			}
		}	
	}

	
	
	/*MAIN*/
	public static void main(String args[]) throws Exception {
		DBProject myApp = new DBProject();
		myApp.menu();
	}
}
