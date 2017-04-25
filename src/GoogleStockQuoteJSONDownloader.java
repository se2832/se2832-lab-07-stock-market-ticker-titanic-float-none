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
 * This class will obtain a stock quote from Google by interpreting a JSON interface and manipulating it into a standard format.
 * 
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import exceptions.WebsiteConnectionError;

public class GoogleStockQuoteJSONDownloader implements
		StockQuoteGeneratorInterface {
	/**
	 * This is the stock symbol to be obtained.
	 */
	private String symbol;

	/*
	 * (non-Javadoc)
	 * 
	 * @see msoe.StockQuoteGeneratorInterface#getCurrentQuote()
	 */
	public StockQuoteInterface getCurrentQuote() throws Exception {
		String quoteURL = "http://www.google.com/finance/info?q=NSE:"
				+ this.symbol + "";
		StockQuoteInterface retVal = null;

		try {
			URL url = new URL(quoteURL);

			InputStream inputStream = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String text = "";
			String line = bufferedReader.readLine();

			while (line != null) {
				text += line + "\n";
				line = bufferedReader.readLine();
			}
			bufferedReader.close();

			retVal = new GoogleJSONStockQuote(text);
		} catch (FileNotFoundException ex) {
			throw new WebsiteConnectionError("Unable to connect with "
					+ quoteURL);
		} catch (MalformedURLException e) {
			throw new WebsiteConnectionError("Unable to connect with "
					+ quoteURL);
		} catch (IOException e) {
			throw new WebsiteConnectionError("Unable to connect with "
					+ quoteURL);
		}

		return retVal;
	}

	/**
	 * @param symbol
	 *            This is the symbol to watch.
	 */
	public GoogleStockQuoteJSONDownloader(String symbol) {
		super();
		this.symbol = symbol;
	}

	/**
	 * Default constructor.
	 */
	public GoogleStockQuoteJSONDownloader() {
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
		return new GoogleStockQuoteJSONDownloader(symbol);
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
