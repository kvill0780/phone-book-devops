package bf.kvill.spring_phone_book.exception;

public class ContactNotFoundException extends RuntimeException {
  public ContactNotFoundException(String message) {
    super(message);
  }
}