package stockapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stockapp.controller.dto.StockDto;
import stockapp.exception.StockException;
import stockapp.repository.StockRepository;
import stockapp.repository.model.Stock;
import stockapp.service.mapper.StockMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static stockapp.exception.StockException.MSG_COULD_NOT_FIND_STOCK;
import static stockapp.exception.StockException.supplier;
import static stockapp.service.mapper.StockMapper.toDto;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final String ERROR_STOCK_ID = "Stock id must not be null";
    private static final Predicate<Stock> nameValidator = stock -> stock.getName().length() > 0;
    private static final Predicate<Stock> priceValidator = stock -> stock.getCurrentPrice().signum() != -1;

    private final StockRepository stockRepository;

    @Transactional
    public List<StockDto> getStocks() {
        return stockRepository.findAll().stream()
                .map(StockMapper::toDto)
                .collect(toList());
    }

    @Transactional
    public StockDto getById(Long id) {
        requireNonNull(id, ERROR_STOCK_ID);

        return stockRepository.findById(id)
                .map(StockMapper::toDto)
                .orElseThrow(supplier(MSG_COULD_NOT_FIND_STOCK));
    }

    @Transactional
    public StockDto updateStockPrice(Long id, BigDecimal price) {
        requireNonNull(id, ERROR_STOCK_ID);

        return stockRepository.findById(id)
                .map(stock -> {
                    stock.setCurrentPrice(price);
                    stock.setLastUpdate(now());
                    return toDto(stockRepository.save(stock));
                })
                .orElseThrow(supplier(MSG_COULD_NOT_FIND_STOCK));
    }

    @Transactional
    public StockDto createStock(String name, BigDecimal price) {
        return Optional.of(Stock.builder().currentPrice(price).name(name).lastUpdate(now()).build())
                .filter(nameValidator.and(priceValidator))
                .map(stock -> toDto(stockRepository.save(stock)))
                .orElseThrow(() -> new StockException("Supplied input is invalid"));
    }
}
