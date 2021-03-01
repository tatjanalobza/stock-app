package stockapp.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stockapp.controller.dto.StockDto;
import stockapp.service.StockService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@Api(value = "stockapp", description = "Application for managing stocks")
public class StockController {

    private final StockService stockService;

    @ApiOperation(value = "List all available stocks")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<StockDto> getStocks() {
        return stockService.getStocks();
    }

    @ApiOperation(value = "Finds a stock by id")
    @GetMapping(value = "/{stockId}", produces = APPLICATION_JSON_VALUE)
    public StockDto getStock(@PathVariable("stockId") Long stockId) {
        return stockService.getById(stockId);
    }

    @ApiOperation(value = "Updates the price of the stock")
    @PutMapping(value = "/{stockId}", produces = APPLICATION_JSON_VALUE)
    public StockDto updateStockPrice(
            @PathVariable("stockId") Long stockId,
            @RequestParam @Min(value = 0, message = "The price cannot be lower than 0.00") BigDecimal price
    ) {
        return stockService.updateStockPrice(stockId, price);
    }

    @ApiOperation(value = "Creates a new stock entry")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public StockDto createStock(
            @RequestParam @NotBlank String name,
            @RequestParam @Min(value = 0, message = "The price cannot be lower than 0.00") BigDecimal price
    ) {
        return stockService.createStock(name, price);
    }
}
