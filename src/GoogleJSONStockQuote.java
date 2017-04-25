import java.util.HashMap;

public class GoogleJSONStockQuote implements StockQuoteInterface {

	private String jsonString;
	private HashMap<String, String> elements = new HashMap<String, String>();

	/**
	 * This constructor will create a new stock quote based on a JSON record
	 * from Google.
	 * 
	 * @param jsonString
	 *            This is the json string that is to be manipulated.
	 */
	public GoogleJSONStockQuote(String jsonString) {
		super();

		// Remove the leading // from the string.
		this.jsonString = jsonString.substring(2);
		// Remove some of the other characters that get in the way of processing the text.
		this.jsonString = this.jsonString.replace('\n', ' ');
		this.jsonString = this.jsonString.replace('{', ' ');
		this.jsonString = this.jsonString.replace('}', ' ');
		this.jsonString = this.jsonString.replace('[', ' ');
		this.jsonString = this.jsonString.replace(']', ' ');

		// SPlit the string into the different fields.
		String[] stringElements = this.jsonString.split(",");
		for (int index = 0; index < stringElements.length; index++) {
			String[] fields = stringElements[index].split(":");
			for (int index1 = 0; index1 < fields.length; index1++) {
				fields[index1] = fields[index1].replaceAll("\"", "").trim();
			}
			// Place the fields into the hash table.
			elements.put(fields[0], fields[1]);
		}
	}

	@Override
	public String getSymbol() {
		return elements.get("t");
	}

	@Override
	public double getLastTrade() {
		return Double.parseDouble(elements.get("l"));
	}

	@Override
	public double getClose() {
		return Double.parseDouble(elements.get("pcls_fix"));
	}

	@Override
	public double getChange() {
		return Double.parseDouble(elements.get("c"));
	}
}
