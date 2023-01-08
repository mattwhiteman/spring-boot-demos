package matt.sb.example.repositories.primary;

import matt.sb.example.entities.primary.UserRecord;
import org.springframework.data.repository.CrudRepository;

public interface UserRecordRepository extends CrudRepository<UserRecord, Integer> {
}
