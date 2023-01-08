package matt.sb.example.services;

import matt.sb.example.entities.primary.UserRecord;
import matt.sb.example.repositories.primary.UserRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRecordRepository mockUserRepository;

    @Test
    public void testGetUserByIdExisting() {
        UserService underTest = new UserService(mockUserRepository);

        when(mockUserRepository.findById(anyInt())).thenReturn(
                Optional.of(new UserRecord(1, "first",
                       "last")));

        UserRecord result = underTest.getUserById(1);

        assertEquals(1, result.getUserId());
        assertEquals("first", result.getFirstName());
        assertEquals("last", result.getLastName());
    }

    @Test
    public void testGetProductByIdNotExisting() {
        UserService underTest = new UserService(mockUserRepository);

        when(mockUserRepository.findById(anyInt())).thenReturn(
                Optional.empty());

        try {
            underTest.getUserById(1);
            fail();
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }
}
