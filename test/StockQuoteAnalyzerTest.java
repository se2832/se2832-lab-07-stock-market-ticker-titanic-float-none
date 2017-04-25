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

    private static final double DELTA = 1e-15;

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
}