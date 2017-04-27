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
 * @version $Rev:: 4                       $:  Revision of last commit
 * @author  $Author:: schilling            $:  Author of last commit
 * $Date:: 2011-6-8 19:51:02 -0#$:  Date of last commit
 * $Log$:
 * Modified code so that constructing a new instance does not automatically call the refresh operation. 
 * 
 * This class is responsible for analyzing a set of stock quotes and making the appropriate decisions about them.
 * 
 */

import exceptions.InvalidAnalysisState;
import exceptions.InvalidStockSymbolException;
import exceptions.StockTickerConnectionError;

/**
 * @author schilling
 * 
 */
public class StockQuoteAnalyzer {
	/**
	 * This variable holds the audio interface that is used in the class for
	 * making sounds.
	 */
	private StockTickerAudioInterface audioPlayer = null;
	private String symbol;
	private StockQuoteGeneratorInterface stockQuoteSource = null;

	private StockQuoteInterface previousQuote = null;
	private StockQuoteInterface currentQuote = null;

	/**
	 * @param symbol
	 *            This is the stock symbol that is being analyzed.
	 * @param stockQuoteSource
	 *            This is the source that is to be used to obtain the stock
	 *            quotes.
	 * @throws InvalidStockSymbolException
	 *             Will be thrown if the symbol for the stock is invalid.
	 * @throws NullPointerException
	 *             Will be thrown if the stock quote source is null.
	 * @throws StockTickerConnectionError
	 *             Will be thrown if the class can not connect to the stock
	 *             quote source.
	 */

	public StockQuoteAnalyzer(String symbol, StockQuoteGeneratorInterface stockQuoteSource,
			StockTickerAudioInterface audioPlayer)
			throws InvalidStockSymbolException, NullPointerException {
		super();

		// Check the validity of the symbol.
		if (StockTickerListing.getSingleton().isValidTickerSymbol(symbol) == true) {
			this.symbol = symbol;
		} else {
			throw new InvalidStockSymbolException("Symbol " + symbol + "not found.");
		}
		if (stockQuoteSource == null) {
			throw new NullPointerException("The source for stock quotes can not be null");
		}
		this.stockQuoteSource = stockQuoteSource;
		this.audioPlayer = audioPlayer;
	}

	/**
	 * Get the latest stock info from the source and analyze it.
	 * 
	 * @throws StockTickerConnectionError
	 *             Will be thrown if the routine is unable to obtain a current
	 *             stock quote.
	 */
	public void refresh() throws StockTickerConnectionError {
		// Get a new quote.
		try {
			StockQuoteInterface temp = this.stockQuoteSource.getCurrentQuote();

			this.previousQuote = currentQuote;
			this.currentQuote = temp;
		} catch (Exception e) {
			throw new StockTickerConnectionError("Unable to connect with Stock Ticker Source.");
		}

	}

	/**
	 * This method will cause the appropriate audio to play back based on how
	 * the stock is doing. If the stock is up by more than 1% since the close,
	 * happy music will be played. If the stock is down by 1% or more, then sad
	 * music will play. However, nothing will happen if the audio player is
	 * null. If for some reason there is an error with the internal state, then
	 * error music will be played.
	 */
	public void playAppropriateAudio() {
		if (audioPlayer != null) {
			try {
				if ((this.getPercentChangeSinceClose() > 0)) {
					audioPlayer.playHappyMusic();
				}
				if ((this.getPercentChangeSinceClose() <= -1)) {
					audioPlayer.playSadMusic();
				}
			} catch (InvalidAnalysisState e) {
				// We have not retrieved a valid quote and are in a mode in
				// which playing happy or sad music does not make sense. Play
				// error music instead.
				audioPlayer.playErrorMusic();
			}
		}
	}

	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * This method will return the previous close for the given stock.
	 * 
	 * @return The previous closing value for the stock will be returned.
	 * @throws InvalidAnalysisState
	 *             An InvalidAnalysisState Exception will be thrown if a quote
	 *             has not yet been retrieved.
	 */

	public double getPreviousClose() throws InvalidAnalysisState {
		if (currentQuote == null) {
			throw new InvalidAnalysisState("No quote has ever been retrieved.");
		}
		return currentQuote.getClose();
	}

	/**
	 * This method will return the current selling price for the given stock.
	 * 
	 * @return The return value will be the last traded value for the given
	 *         stock, otherwise known as the current price.
	 * @throws InvalidAnalysisState
	 *             An InvalidAnalysisState Exception will be thrown if a quote
	 *             has not yet been retrieved.
	 */

	public double getCurrentPrice() throws InvalidAnalysisState {
		if (currentQuote == null) {
			throw new InvalidAnalysisState("No quote has ever been retrieved.");
		}
		return currentQuote.getLastTrade();
	}

	/**
	 * This method will return the change since the previous close for the given
	 * stock.
	 * 
	 * @return The change in dollars will be returned.
	 * @throws InvalidAnalysisState
	 *             An InvalidAnalysisState Exception will be thrown if a quote
	 *             has not yet been retrieved.
	 */
	public double getChangeSinceClose() throws InvalidAnalysisState {
		if (currentQuote == null) {
			throw new NullPointerException("No quote has ever been retrieved.");
		}
		return currentQuote.getChange()-currentQuote.getClose();
	}

	/**
	 * This method will return the percent change for the given stock.
	 * 
	 * @return The percent change for the given stock will be returned. It will
	 *         be accurate to the nearest .01%.
	 * @throws InvalidAnalysisState
	 *             An InvalidAnalysisState Exception will be thrown if a quote
	 *             has not yet been retrieved.
	 */
	public double getPercentChangeSinceClose() throws InvalidAnalysisState {
		if (currentQuote == null) {
			throw new InvalidAnalysisState("No quote has ever been retrieved.");
		}

		return Math.round((100000 * this.currentQuote.getChange() / this.currentQuote.getClose())) / 100.0;
	}

	/**
	 * This method will return the change in price since the last check. If the
	 * market is open, this will be the change that has occurred since the last
	 * time the value was checked. If the market is closed or the value has not
	 * changed since the last check, then this will be 0.
	 * 
	 * @return The raw changed value for the given stock will be returned.
	 * @throws InvalidAnalysisState
	 *             An invalid analysis state will be thrown if there have not
	 *             been two successful updates retrieved from the stock quote
	 *             data source.
	 */
	public double getChangeSinceLastCheck() throws InvalidAnalysisState {
		if (previousQuote != null){
			return currentQuote.getLastTrade() - previousQuote.getLastTrade();
		} else {
			throw new InvalidAnalysisState();
		}

	}
}
