package stockapp.service;

import com.google.common.collect.ImmutableList;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stockapp.exception.StockException;
import stockapp.repository.StockRepository;
import stockapp.repository.model.Stock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    void testGetStocks() {
        when(stockRepository.findAll()).thenReturn(ImmutableList.of(
                Stock.builder().name("test stock_01").currentPrice(BigDecimal.ONE).build(),
                Stock.builder().name("test stock_02").currentPrice(BigDecimal.TEN).build()
        ));

        assertThat(stockService.getStocks()).hasSize(2)
                .extracting("name", "currentPrice")
                .containsExactly(
                        Tuple.tuple("test stock_01", BigDecimal.ONE),
                        Tuple.tuple("test stock_02", BigDecimal.TEN)
                );

        verify(stockRepository).findAll();
    }

    @Test
    void testGetStocks_EmptyList() {
        when(stockRepository.findAll()).thenReturn(ImmutableList.of());

        assertThat(stockService.getStocks()).isEmpty();

        verify(stockRepository).findAll();
    }

    @Test
    void testGetById() {
        when(stockRepository.findById(any(Long.class))).thenReturn(Optional.of(
                Stock.builder().name("test").currentPrice(BigDecimal.TEN).build()
        ));

        assertThat(stockService.getById(12L))
                .extracting("name", "currentPrice")
                .containsExactly("test", BigDecimal.TEN);
    }

    @Test
    void testGetById_EmptyStock() {
        when(stockRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(StockException.class, () -> stockService.getById(12L));

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "Stock could not be found");
    }

    @Test
    void testGetById_InvalidId() {
        Exception exception = assertThrows(NullPointerException.class, () -> stockService.getById(null));

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "Stock id must not be null");

        verifyNoInteractions(stockRepository);
    }

    @Test
    void testUpdateStockPrice() {
        when(stockRepository.findById(eq(123L))).thenReturn(Optional.of(
                Stock.builder().id(123L).name("test").currentPrice(BigDecimal.TEN).build()
        ));
        when(stockRepository.save(any(Stock.class))).thenAnswer(input -> input.getArgument(0));

        assertThat(stockService.updateStockPrice(123L, BigDecimal.valueOf(456))).isNotNull()
                .extracting("id", "name", "currentPrice")
                .containsExactly(123L, "test", BigDecimal.valueOf(456));

        verify(stockRepository).findById(eq(123L));
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void testUpdateStockPrice_InvalidId() {
        Exception exception = assertThrows(
                NullPointerException.class,
                () -> stockService.updateStockPrice(null, BigDecimal.valueOf(456))
        );

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "Stock id must not be null");

        verifyNoInteractions(stockRepository);
    }

    @Test
    void testUpdateStockPrice_InvalidPrice() {
        when(stockRepository.findById(eq(123L))).thenReturn(Optional.of(
                Stock.builder().id(123L).name("test").currentPrice(BigDecimal.TEN).build()
        ));

        Exception exception = assertThrows(
                NullPointerException.class,
                () -> stockService.updateStockPrice(123L, null)
        );

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "currentPrice is marked non-null but is null");

        verify(stockRepository).findById(eq(123L));
        verifyNoMoreInteractions(stockRepository);
    }

    @Test
    void testCreateStock() {
        when(stockRepository.save(any(Stock.class))).thenAnswer(input -> input.getArgument(0));

        assertThat(stockService.createStock("test", BigDecimal.valueOf(456))).isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("name", "test")
                .hasFieldOrPropertyWithValue("currentPrice", BigDecimal.valueOf(456));

        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void testCreateStock_NullName() {
        Exception exception = assertThrows(
                NullPointerException.class,
                () -> stockService.createStock(null, BigDecimal.valueOf(456))
        );

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "name is marked non-null but is null");

        verifyNoInteractions(stockRepository);
    }

    @Test
    void testCreateStock_BlankName() {
        Exception exception = assertThrows(
                StockException.class,
                () -> stockService.createStock("", BigDecimal.valueOf(456))
        );

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "Supplied input is invalid");

        verifyNoInteractions(stockRepository);
    }

    @Test
    void testCreateStock_NullPrice() {
        Exception exception = assertThrows(
                NullPointerException.class,
                () -> stockService.createStock("test", null)
        );

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "currentPrice is marked non-null but is null");

        verifyNoInteractions(stockRepository);
    }

    @Test
    void testCreateStock_NegativePrice() {
        Exception exception = assertThrows(
                StockException.class,
                () -> stockService.createStock("test",  BigDecimal.valueOf(-456))
        );

        assertThat(exception).isNotNull()
                .hasFieldOrPropertyWithValue("message", "Supplied input is invalid");

        verifyNoInteractions(stockRepository);
    }

    @Test
    void testCreateStock_ZeroPrice() {
        when(stockRepository.save(any(Stock.class))).thenAnswer(input -> input.getArgument(0));

        assertThat(stockService.createStock("test", BigDecimal.ZERO)).isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("name", "test")
                .hasFieldOrPropertyWithValue("currentPrice", BigDecimal.ZERO);

        verify(stockRepository).save(any(Stock.class));
    }

}
