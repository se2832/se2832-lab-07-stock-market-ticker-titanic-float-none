import exceptions.InvalidAnalysisState;
import exceptions.InvalidStockSymbolException;
import exceptions.StockTickerConnectionError;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class StockQuoteAnalyzerTest {
    @Mock
    private StockQuoteGeneratorInterface generatorMock;
    @Mock
    private StockTickerAudioInterface audioMock;

    private StockQuoteAnalyzer analyzer;

    private static final double DELTA = 1e-5;

    @BeforeMethod
    public void setUp() throws Exception {
        generatorMock = mock(StockQuoteGeneratorInterface.class);
        audioMock = mock(StockTickerAudioInterface.class);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        generatorMock = null;
        audioMock = null;
    }

    @Test(expectedExceptions = InvalidStockSymbolException.class)
    public void constructorShouldThrowExceptionWhenSymbolIsInvalid() throws Exception {
        analyzer = new StockQuoteAnalyzer("ZZZZZZZZZ", generatorMock, audioMock);
    }

    @Test
    public void constructorShouldInitalizeCorrectlyWhenSymbolIsValid() throws Exception {
        analyzer = new StockQuoteAnalyzer("HOG", generatorMock, audioMock);
    }

    @Test (expectedExceptions = NullPointerException.class)
    public void constructorShouldThrowExceptionWhenAudioIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("HOG", null, audioMock);
    }


    @Test
    public void refreshShouldRefreshStockPriceWhenCalled() throws Exception {
        analyzer = new StockQuoteAnalyzer("HOG", generatorMock, audioMock);

        double prevClose = 56.57;
        double lastTrade = 57.21;
        double change = .89;

        StockQuoteInterface newValue = new StockQuote("HOG", prevClose, lastTrade, change);
        when(generatorMock.getCurrentQuote()).thenReturn(newValue);

        analyzer.refresh();
        verify(generatorMock, times(1)).getCurrentQuote();
    }

    @Test (expectedExceptions = StockTickerConnectionError.class)
    public void refreshShouldThrowExceptionWhenProblemOccurred() throws Exception {
        analyzer = new StockQuoteAnalyzer("HOG", generatorMock, audioMock);

        when(generatorMock.getCurrentQuote()).thenThrow(Exception.class);
        analyzer.refresh();
    }

    @Test
    public void getSymbolShouldReturnAppropriateSymbol() throws Exception {
        // Arrange
        analyzer = new StockQuoteAnalyzer("CBD", generatorMock, audioMock);

        // Act
        String symbol = analyzer.getSymbol();

        // Assert
        assertEquals("CBD", symbol);
    }

    @Test
    public void getPreviousCloseShouldReturnPreviousClosingValue() throws Exception {
        // Arrange
        analyzer = new StockQuoteAnalyzer("CBD", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("CBD", 35.5, 40.0, 5.0);

        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote);

        // Act
        analyzer.refresh();
        double closeValue = analyzer.getPreviousClose();

        // Assert
        assertEquals(closeValue, 35.5, DELTA);
    }

    @Test (expectedExceptions = InvalidAnalysisState.class)
    public void getPreviousCloseShouldThrowExceptionWhenNotRefreshed() throws Exception {
        // Arrange
        analyzer = new StockQuoteAnalyzer("CBD", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("CBD", 35.5, 40.0, 5.0);

        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote);

        // Act
        double closeValue = analyzer.getPreviousClose();
    }

    @Test
    public void getCurrentPriceShouldReturnAccuratePriceWhenCalled() throws Exception {
        // Arrange
        analyzer = new StockQuoteAnalyzer("CBD", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("CBD", 35.5, 40.0, 5.0);

        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote);

        // Act
        analyzer.refresh();
        double closeValue = analyzer.getCurrentPrice();

        // Act
        assertEquals(closeValue, 40.0, DELTA);
    }

    @Test (expectedExceptions = InvalidAnalysisState.class)
    public void getCurrentPriceShouldThrowExceptionWhenNotRefreshed() throws Exception {
        // Arrange
        analyzer = new StockQuoteAnalyzer("CBD", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("CBD", 35.5, 40.0, 5.0);

        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote);

        // Act
        double closeValue = analyzer.getCurrentPrice();
    }

    @Test (expectedExceptions = InvalidAnalysisState.class)
    public void getChangeSinceCloseShouldThrowExceptionWhenNotRefreshed() throws Exception {
        // Arrange
        analyzer = new StockQuoteAnalyzer("CBD", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("CBD", 35.5, 40.0, 5.0);

        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote);

        // Act
        double closeValue = analyzer.getChangeSinceClose();
    }

    @Test
    public void getChangeSinceCloseShouldReturnAccurateChangeSinceClose() throws Exception {
        // Arrange
        analyzer = new StockQuoteAnalyzer("CBD", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("CBD", 35.5, 40.0, 5.0);

        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote);

        // Act
        analyzer.refresh();
        double closeValue = analyzer.getChangeSinceClose();

        // Assert
        assertEquals(closeValue, 5.0, DELTA);
    }

    @Test
    public void getChangeSinceLastCheckShouldReturnDifferenceWhenCalled() throws Exception{
        analyzer = new StockQuoteAnalyzer("HOG", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("HOG", 35.5,40.0,5.0);
        StockQuoteInterface stockQuote1 = new StockQuote("HOG", 35.5, 43.2,8.2);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote).thenReturn(stockQuote1);

        //act
        analyzer.refresh();
        analyzer.refresh();
        double lastCheckDifference = analyzer.getChangeSinceLastCheck();

        assertEquals(lastCheckDifference, 3.2, DELTA);
    }

    @Test (expectedExceptions = InvalidAnalysisState.class)
    public void getChangeSinceLastCheckShouldThrowExceptionWhenCalledPrematurly() throws Exception{
        analyzer = new StockQuoteAnalyzer("HOG", generatorMock, audioMock);
        StockQuoteInterface stockQuote = new StockQuote("HOG", 35.5,40.0,5.0);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuote);

        //act
        analyzer.refresh();

        //assert
        analyzer.getChangeSinceLastCheck();
    }
}