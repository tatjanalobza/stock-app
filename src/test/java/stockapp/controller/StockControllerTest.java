package stockapp.controller;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import stockapp.controller.dto.StockDto;
import stockapp.service.StockService;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    void testGetStocks() throws Exception {
        when(stockService.getStocks()).thenReturn(ImmutableList.of(
                StockDto.builder().id(1L).name("test stock_01").currentPrice(ONE).build(),
                StockDto.builder().id(2L).name("test stock_02").currentPrice(TEN).build()
        ));

        this.mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("test stock_01")))
                .andExpect(jsonPath("$[0].currentPrice", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("test stock_02")))
                .andExpect(jsonPath("$[1].currentPrice", is(10)));

        verify(stockService).getStocks();
    }

    @Test
    void testGetStock() throws Exception {
        when(stockService.getById(eq(1L))).thenReturn(
                StockDto.builder().id(1L).name("test stock_01").currentPrice(ONE).build());

        this.mockMvc.perform(get("/stocks/{stockId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test stock_01")))
                .andExpect(jsonPath("$.currentPrice", is(1)));

        verify(stockService).getById(eq(1L));
    }

    @Test
    void testUpdateStock() throws Exception {
        when(stockService.updateStockPrice(eq(1L), eq(TEN))).thenReturn(
                StockDto.builder().id(1L).name("test stock_01").currentPrice(TEN).build());

        this.mockMvc.perform(put("/stocks/{stockId}", 1L).param("price", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test stock_01")))
                .andExpect(jsonPath("$.currentPrice", is(10)));

        verify(stockService).updateStockPrice(eq(1L), eq(TEN));
    }

    @Test
    void testCreateStock() throws Exception {
        when(stockService.createStock(eq("test stock 01"), eq(TEN))).thenReturn(
                StockDto.builder().id(1L).name("test stock_01").currentPrice(TEN).build());

        this.mockMvc.perform(post("/stocks")
                .param("name", "test stock 01").param("price", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test stock_01")))
                .andExpect(jsonPath("$.currentPrice", is(10)));

        verify(stockService).createStock(eq("test stock 01"), eq(TEN));
    }

}
