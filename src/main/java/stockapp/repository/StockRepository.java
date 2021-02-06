package stockapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stockapp.repository.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
