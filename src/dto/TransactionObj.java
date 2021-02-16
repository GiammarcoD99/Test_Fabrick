package dto;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionObj {
	
	private String transactionId;
	private String operationId;
	private String accountingDate;
	private String valueDate;
	private TransactionTypeObj type;
	
	private String amount;
	private String currency;
	private String description;
	
	
	public TransactionObj valorizeField( JSONObject jsonObj ) throws JSONException {
		
		TransactionObj obj = new TransactionObj();
		TransactionTypeObj tranTypeObj = new TransactionTypeObj();
		
		obj.setAccountingDate( jsonObj.getString("accountingDate") );
		obj.setAmount( jsonObj.get("amount").toString() );
		obj.setOperationId( jsonObj.getString("operationId") );
		obj.setDescription( jsonObj.getString("description") );
		obj.setCurrency( jsonObj.getString("currency") );
		obj.setValueDate( jsonObj.getString("valueDate") );
		
		tranTypeObj.setEnumeration( jsonObj.getJSONObject("type").getString("enumeration") );
		tranTypeObj.setValue( jsonObj.getJSONObject("type").getString("value") );
		
		obj.setType(tranTypeObj);
		obj.setTransactionId( jsonObj.getString("transactionId") );
		
		return obj;
	}
	
	
	@Override
	public String toString() {
		return "[ \n"
				+ "  transactionId = " + transactionId+ ",\n"
				+ "  operationId = " + operationId + ",\n"
				+ "  accountingDate = " + accountingDate + ",\n"
				+ "  valueDate = " + valueDate + ",\n"
				+ "  type = " + type.toString() + ",\n"
				+ "  amount = " + amount + ",\n"
				+ "  currency = " + currency + ",\n"
				+ "  description = " + description+"\n"
				+" ]\n";
	}
	
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	
	public String getOperationId() {
		return operationId;
	}
	
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	
	
	public String getAccountingDate() {
		return accountingDate;
	}
	
	public void setAccountingDate(String accountingDate) {
		this.accountingDate = accountingDate;
	}
	
	
	public String getValueDate() {
		return valueDate;
	}
	
	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}
		
	
	public TransactionTypeObj getType() {
		return type;
	}
	
	public void setType(TransactionTypeObj type) {
		this.type = type;
	}
	
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
