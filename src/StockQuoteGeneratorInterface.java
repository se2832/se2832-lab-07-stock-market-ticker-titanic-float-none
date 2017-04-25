/**
 * (c) Copyright 2008, Dr. Walter W. Schilling, Jr.
 * ALL RIGHTS RESERVED 
 * Permission to use, copy, modify, and distribute this software for 
 * any purpose and without fee is hereby granted, provided that the above
 * copyright notice appear in all copies and that both the copyright notice
 * and this permission notice appear in supporting documentation, and that 
 * the name of Walter W. Schilling, Jr. not be used in advertising
 * or publicity pertaining to distribution of the software without specific,
 * written prior permission. 
 *
 * THE MATERIAL EMBODIED ON THIS SOFTWARE IS PROVIDED TO YOU "AS-IS"
 * AND WITHOUT WARRANTY OF ANY KIND, EXPRESS, IMPLIED OR OTHERWISE,
 * INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE.  IN NO EVENT SHALL MSOE
 * BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DIRECT,
 * SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER, INCLUDING WITHOUT LIMITATION,
 * LOSS OF PROFIT, LOSS OF USE, SAVINGS OR REVENUE, OR THE CLAIMS OF
 * THIRD PARTIES, WHETHER OR NOT WALTER SCHILLING HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS, HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE
 * POSSESSION, USE OR PERFORMANCE OF THIS SOFTWARE. 
 * 
 * @version $Rev:: 3                       $:  Revision of last commit
 * @author  $Author:: schilling            $:  Author of last commit
 * $Date:: 2008-10-20 19:51:02 -0#$:  Date of last commit
 * $Log$:
 * 
 * This class stores information relative to an address.
 * 
 */


public interface StockQuoteGeneratorInterface {

	/**
	 * This routine will get a current stock quote.
	 * 
	 * @return RFeturn a current stock quote for the given stock.
	 * @throws Exception
	 *             An exception will be thrown if a current stock quote can not
	 *             be obtained.
	 */
	public abstract StockQuoteInterface getCurrentQuote() throws Exception;

	/**
	 * @return Obtain the symbol that is being watched.
	 */
	public abstract String getSymbol();

	/**
	 * Set the stock symbol that is to be watched.
	 * 
	 * @param symbol
	 *            This is the symbol that is to be watched.
	 */
	public abstract void setSymbol(String symbol);

	/**
	 * This routine fakes a static method that is capable of creating a new
	 * instance of the given class. It will return a stock quote generator
	 * interface which watches the given string. How this is done is up to the
	 * given class.
	 * 
	 * @param symbol
	 *            This is the symbol that is to be watched.
	 * @return A new StockQuoteGeneratorInterface realization will be returned.
	 */
	public abstract StockQuoteGeneratorInterface createNewInstance(String symbol);

}