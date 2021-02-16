package unitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import code.CodeClass;

public class TestClass {
	
	CodeClass codeClass = new CodeClass();
	private static final String transfJsonAmountExc = "{"  
												      + "\"creditor\": {"  
												    	+ "\"name\": \"John Doe\"," 
												    	+ "\"account\": {"  
												    		+ "\"accountCode\": \"IT66R0200841133000420092892\","
												    		+ "\"bicCode\": \"UNCRITM1NL7\""
												    		+ "}"  
												    	+ "}," 
												      + "\"executionDate\": \"#TOMORROW#\","
												      + "\"description\": \"Payment invoice 75/2017\"," 
												      + "\"amount\": 1000000," 
												      + "\"currency\": \"EUR\"" 
												      + "}";
	
	// TEST FUNZIONI
	
	@Test
	public void testInitConnection() throws IOException {
		String path = CodeClass.baseUrl + CodeClass.accountId + "/balance";
		
		HttpURLConnection con = codeClass.initConnection("GET", path);
		
		assertEquals( 200, con.getResponseCode() );
		
	}
	
	
	@Test
	public void testGetResponseSuccess() throws IOException, JSONException {
		String dateFrom = "2019-01-01";
		String dateTo = "2019-12-01";
		String path = CodeClass.baseUrl + CodeClass.accountId
				      + "/transactions?fromAccountingDate=" + dateFrom
				      + "&toAccountingDate=" + dateTo;
		
		HttpURLConnection con = codeClass.initConnection("GET", path);
		con.setDoOutput(true);
		
		BufferedReader buffReader = codeClass.initBuffer(con);
		
		StringBuilder stringBuilder = codeClass.getResponse(con, buffReader);
		
		con.disconnect();
		
		JSONObject jsonObject = new JSONObject( stringBuilder.toString() );
		
		assertEquals( "OK", jsonObject.getString("status") );
	}
	
	
	@Test
	public void testGetResponseFailure() throws IOException, JSONException {
		String path = CodeClass.baseUrl + CodeClass.accountId + "/payments/money-transfers";
		
		HttpURLConnection con = codeClass.initConnection("POST", path);
		con.setDoOutput(true);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			OutputStream os = con.getOutputStream();
		    byte[] input = transfJsonAmountExc.replace( "#TOMORROW#", codeClass.getTomorrowDate() ).getBytes("utf-8");
		    
		    os.write(input, 0, input.length);			
	        
		    BufferedReader buffReader = codeClass.initBuffer(con);
		    
		    stringBuilder = codeClass.getResponse(con, buffReader);
		    
		} catch(IOException  ioExc) {
			System.out.println(" ** ERROR ** - " + ioExc);
		}
		
		con.disconnect();
		
		JSONObject jsonObject = new JSONObject( stringBuilder.toString() );
		JSONObject errors = jsonObject.getJSONArray("errors").getJSONObject(0);
		
		String desErrorExp = "Invalid Amount: Transaction Limit Exceeding";
		
		assertEquals( "REQ022", errors.getString("code") );
		assertEquals( desErrorExp, errors.getString("description") );
	}
	
		
	// TEST METODI PRINCIPALI
	
	@Test
	public void letturaSaldo() throws IOException, JSONException {
		assertNotNull( codeClass.leggiSaldo() );
	}
	
	
	@Test
	public void verificaBonifico() throws IOException, JSONException {
		assertNotNull( codeClass.statoBonifico() );
	}
	
	
	//Il numero delle transazioni alcune volte era 0, puo' causare un esito negativo
	@Test
	public void letturaTransazioni() throws IOException, JSONException {
		String dateFrom = "2019-01-01";
		String dateTo = "2019-12-01";
		
		JSONArray transactions = codeClass.leggiTransazioni( dateFrom, dateTo );
		
		assertEquals( 14, transactions.length() );
	}
	
}
