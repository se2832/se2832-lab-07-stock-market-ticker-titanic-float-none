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
 * This class will extract a Stock quote from the HTML baseline material on the website.
 * 
 */

import exceptions.WebsiteConnectionError;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class YahooStockQuoteHTMLQuoteExtractor implements StockQuoteGeneratorInterface {
	/**
	 * This is the stock symbol to be obtained.
	 */
	/**
	 * 
	 */
	private String symbol;

	private static final int SYMBOL = 0;
	private static final int LASTTRADE = 1;
	/* Not used but left in for future need. */
	/*
	 * private static final int DATE = 2; private static final int TIME = 3;
	 */
	private static final int CHANGE = 4;
	// private static final int OPEN = 5;
	/*
	 * private static final int HIGH = 6; private static final int LOW = 7;
	 * private static final int VOLUME = 8;
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see msoe.StockQuoteGeneratorInterface#getCurrentQuote()
	 */
	public StockQuoteInterface getCurrentQuote() throws Exception {
		String quoteURL = "http://download.finance.yahoo.com/d/quotes.csv?s=" + this.symbol + "&f=sl1d1t1c1ohgv&e=.csv";
		StockQuote retVal = null;

		try {
			URL url = new URL(quoteURL);

			InputStream inputStream = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String text = "";
			String line = bufferedReader.readLine();

			while (line != null) {
				text += line + "\n";
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			String fields[] = text.split(",");

			double lastTrade = Double.parseDouble(fields[YahooStockQuoteHTMLQuoteExtractor.LASTTRADE]);
			double change = Double.parseDouble(fields[YahooStockQuoteHTMLQuoteExtractor.CHANGE]);
			double previosClose = lastTrade - change;

			retVal = new StockQuote(fields[YahooStockQuoteHTMLQuoteExtractor.SYMBOL], previosClose, lastTrade, change);

		} catch (FileNotFoundException ex) {
			throw new WebsiteConnectionError("Unable to connect with " + quoteURL);
		} catch (MalformedURLException e) {
			throw new WebsiteConnectionError("Unable to connect with " + quoteURL);
		} catch (IOException e) {
			throw new WebsiteConnectionError("Unable to connect with " + quoteURL);
		}

		return retVal;
	}

	/**
	 * @param symbol
	 *            This is the symbol to watch.
	 */
	public YahooStockQuoteHTMLQuoteExtractor(String symbol) {
		super();
		this.symbol = symbol;
	}

	/**
	 * Default constructor.
	 */
	public YahooStockQuoteHTMLQuoteExtractor() {
		super();
		this.symbol = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.msoe.se2831.lab6.StockQuoteGeneratorInterface#createNewInstance(java
	 * .lang.String)
	 */
	public StockQuoteGeneratorInterface createNewInstance(String symbol) {
		return new YahooStockQuoteHTMLQuoteExtractor(symbol);
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @param symbol
	 *            the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
