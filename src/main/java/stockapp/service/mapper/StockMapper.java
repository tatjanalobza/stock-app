package stockapp.service.mapper;

import stockapp.controller.dto.StockDto;
import stockapp.repository.model.Stock;

public class StockMapper {

    public static StockDto toDto(Stock stock){
        if (stock == null) return null;

        return StockDto.builder()
                .name(stock.getName())
                .currentPrice(stock.getCurrentPrice())
                .id(stock.getId())
                .build();
    }
}
