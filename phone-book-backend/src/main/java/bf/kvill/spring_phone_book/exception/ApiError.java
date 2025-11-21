package bf.kvill.spring_phone_book.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ApiError {
    private String message;
    private int code;
    private Timestamp timestamp;
}
