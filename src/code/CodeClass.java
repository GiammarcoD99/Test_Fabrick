package code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dto.TransactionObj;


public class CodeClass {
	
	public static final String baseUrl = "https://sandbox.platfr.io/api/gbs/banking/v4.0/accounts/";
	public static final Long accountId = 14537780L;
	
	public static String sampleTransferJson = "{"  
								      + "\"creditor\": {"  
								    	+ "\"name\": \"John Doe\"," 
								    	+ "\"account\": {"  
								    		+ "\"accountCode\": \"IT66R0200841133000420092892\","
								    		+ "\"bicCode\": \"UNCRITM1NL7\""
								    		+ "}"  
								    	+ "}," 
								      + "\"executionDate\": \"#TOMORROW#\","
								      + "\"description\": \"Payment invoice 75/2017\"," 
								      + "\"amount\": 10," 
								      + "\"currency\": \"EUR\"" 
								      + "}";

	
	public String leggiSaldo() throws IOException, JSONException {
		System.out.println("\n ----- Lettura saldo -----\n");
		
		String path = baseUrl + accountId + "/balance";
		
		HttpURLConnection con = initConnection("GET", path);
		BufferedReader buffReader = initBuffer(con);
		
		StringBuilder stringBuilder = getResponse(con, buffReader);
		
		con.disconnect();

		JSONObject jsonObject = new JSONObject( stringBuilder.toString() );
		String status =  jsonObject.getString("status");
		String balance = null;
		
		if ( status.equals("OK") ) {
			JSONObject payload = jsonObject.getJSONObject("payload");
			
			balance = payload.get("balance").toString();
			String curr = payload.get("currency").toString();
			
			System.out.println(" - Saldo: " + ( !balance.isEmpty() ? balance : "0" ) + " " + curr + "\n");
			
		} else {
			showErrors( jsonObject );
		}
		
		return balance;
	}
	
	
	public String statoBonifico() throws IOException, JSONException {
		System.out.println("\n ----- Verifica stato bonifico -----\n");
		
		String path = baseUrl + accountId + "/payments/money-transfers";
		
		HttpURLConnection con = initConnection("POST", path);
		con.setDoOutput(true);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			//request
			OutputStream os = con.getOutputStream();
		    byte[] input = sampleTransferJson.replace( "#TOMORROW#", getTomorrowDate() ).getBytes("utf-8");
		    
		    os.write(input, 0, input.length);			
	        
		    //response
		    BufferedReader buffReader = initBuffer(con);
		    
		    stringBuilder = getResponse(con, buffReader);
		    
		} catch(IOException  ioExc) {
			System.out.println(" ** ERROR ** - " + ioExc);
		}
		
		con.disconnect();
		
		JSONObject jsonObject = new JSONObject( stringBuilder.toString() );
		String status =  jsonObject.getString("status");
		String esito = null;
		
		if ( status.equals("OK") ) {
			esito =  jsonObject.getJSONObject("payload").get("status").toString();
		    
			System.out.println(" - Stato: " + status + " ["+ esito + "]\n");
			
		} else {
			showErrors( jsonObject );
		}
		
		return esito;
	}
	
	
	public JSONArray leggiTransazioni(String dateFrom, String dateTo) throws IOException, JSONException {
		System.out.println("\n ----- Lettura transazioni -----\n");
		
		String path = baseUrl + accountId
				      + "/transactions?fromAccountingDate=" + dateFrom
				      + "&toAccountingDate=" + dateTo;
		
		HttpURLConnection con = initConnection("GET", path);
		
		BufferedReader buffReader = initBuffer(con);
		
		StringBuilder stringBuilder = getResponse(con, buffReader);
		
		con.disconnect();
		
		JSONObject jsonObject = new JSONObject( stringBuilder.toString() );
		String status =  jsonObject.getString("status");
		JSONArray transactions = null;
				
		if ( status.equals("OK") ) {
			transactions = jsonObject.getJSONObject("payload").getJSONArray("list");
		
			if (transactions.length() == 0 || transactions == null) {
				System.out.println(" - Nessuna transazione trovata \n");
				
				return transactions;
			}
			
			List<TransactionObj> listObj = new ArrayList<TransactionObj>();
			
			for (int i = 0; i < transactions.length(); i++){ 
				jsonObject = transactions.getJSONObject(i);
				TransactionObj tranObj = new TransactionObj();
				
				listObj.add( tranObj.valorizeField(jsonObject) );
				System.out.println( " - Dettagli transazione " + (i + 1) + " :\n" + listObj.get(i).toString() );
			}
		} else {
			showErrors( jsonObject );
		}
		
		return transactions;
	}
	
	
	public HttpURLConnection initConnection( String requestMethod, String path ) throws IOException {
		
		URL url = new URL(path);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setRequestMethod(requestMethod);
		con.setConnectTimeout(5000);
		con.setRequestProperty("apikey", "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP");
		con.setRequestProperty("Auth-Schema", "S2S");
		
		if ( requestMethod.equals("POST") ) {
			con.setRequestProperty("X-Time-Zone", "Europe/Rome");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
	        con.setRequestProperty("Accept", "application/json");
		}
		
		return con;
	}
	
	
	public BufferedReader initBuffer( HttpURLConnection con ) {
		
		BufferedReader br = null; 
		
		try {
			if (100 <= con.getResponseCode() && con.getResponseCode() <= 399) {
		        br = new BufferedReader( new InputStreamReader(con.getInputStream(), "utf-8") );
		        
		    } else {
		        br = new BufferedReader( new InputStreamReader(con.getErrorStream(), "utf-8") );
		    }
		} catch(IOException  ioExc) {
			System.out.println(" ** ERROR ** - "+ioExc);
		}
		
		return br;
	}
	
	
	public StringBuilder getResponse( HttpURLConnection con, BufferedReader in ) throws IOException {
		
		StringBuilder strBld = new StringBuilder();
		
		try {
			String inputLine = null;
			
			while ( (inputLine = in.readLine()) != null ) {
				strBld.append(inputLine);
			}
			
			in.close();
			
		} catch (IOException ioExc) {
			System.out.println(" ** ERROR ** - " + ioExc);
		}
		
		return strBld;
	}
	
	
	public void showErrors( JSONObject jsonObject ) throws JSONException {
		try {
			JSONObject errors = jsonObject.getJSONArray("errors").getJSONObject(0);
			
			System.out.println(" ** Errore chiamata **\n"
							   + " codice = " + errors.getString("code") + "\n"
							   + " descrizione = " + errors.getString("description")
							   );
			
		} catch (JSONException jsonExc) {
			System.out.println(" ** ERROR ** - "+jsonExc);
		}
	}
	
	
	public String getTomorrowDate() {
	    Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 
	    
	    calendar.add(Calendar.DAY_OF_YEAR, 1);
	    Date tomorrow = calendar.getTime();
	    
	    return formatter.format(tomorrow);
	}
	
}
