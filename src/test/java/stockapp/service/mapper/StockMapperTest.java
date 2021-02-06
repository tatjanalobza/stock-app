package stockapp.service.mapper;

import org.junit.jupiter.api.Test;
import stockapp.repository.model.Stock;

import static java.math.BigDecimal.TEN;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static stockapp.service.mapper.StockMapper.toDto;

class StockMapperTest {

    @Test
    void testToDto() {
        Stock input = Stock.builder()
                .id(123L)
                .name("test stock")
                .currentPrice(TEN)
                .lastUpdate(now())
                .build();

        assertThat(toDto(input)).isNotNull()
                .hasFieldOrPropertyWithValue("id", 123L)
                .hasFieldOrPropertyWithValue("name", "test stock")
                .hasFieldOrPropertyWithValue("currentPrice", TEN);
    }

    @Test
    void testToDto_NullInput() {
        assertThat(toDto(null)).isNull();
    }

}
