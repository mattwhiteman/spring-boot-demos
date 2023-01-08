package matt.sb.example.repositories.secondary;

import matt.sb.example.entities.secondary.ProductRecord;
import org.springframework.data.repository.CrudRepository;

public interface ProductRecordRepository extends CrudRepository<ProductRecord, Integer> {
}
