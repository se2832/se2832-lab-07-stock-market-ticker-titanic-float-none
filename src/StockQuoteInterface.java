
/**
 * This interface will return pertinent information about a given stock quote.
 * 
 * @author schilling
 *
 */
public interface StockQuoteInterface {

	/**
	 * This will return the stock symbol for the given quote.
	 * 
	 * @return the symbol
	 */
	public abstract String getSymbol();

	/**
	 * This will return the last trade for the stock.
	 * 
	 * @return the lastTrade
	 */
	public abstract double getLastTrade();

	/**
	 * This will return the previous close for the stock.
	 * 
	 * @return the close
	 */
	public abstract double getClose();

	/**
	 * This will return the changed value for the given stock versus the
	 * previous close.
	 * 
	 * @return The return will be the absolute change in dollars for the given
	 *         stock.
	 */
	public abstract double getChange();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

}