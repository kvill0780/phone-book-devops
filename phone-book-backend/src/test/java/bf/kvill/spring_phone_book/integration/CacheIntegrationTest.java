package bf.kvill.spring_phone_book.integration;

import bf.kvill.spring_phone_book.model.Contact;
import bf.kvill.spring_phone_book.model.User;
import bf.kvill.spring_phone_book.service.ContactService;
import bf.kvill.spring_phone_book.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Disabled("Requires MySQL and Redis - run with Docker Compose")
class CacheIntegrationTest {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldCacheContactsForUser() {
        User user = userService.createUser("testuser", "password");
        
        var contacts1 = contactService.getAllContactsForUser(user);
        
        var cache = cacheManager.getCache("contacts");
        assertNotNull(cache);
        assertNotNull(cache.get("user:" + user.getId()));
        
        var contacts2 = contactService.getAllContactsForUser(user);
        
        assertEquals(contacts1.size(), contacts2.size());
    }

    @Test
    void shouldEvictCacheOnContactCreation() {
        User user = userService.createUser("testuser2", "password");
        contactService.getAllContactsForUser(user);
        
        var cache = cacheManager.getCache("contacts");
        assertNotNull(cache.get("user:" + user.getId()));
        
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setPhoneNumber("+1234567890");
        contactService.createContact(contact, user);
        
        assertNull(cache.get("user:" + user.getId()));
    }

    @Test
    void shouldCacheSearchResults() {
        User user = userService.createUser("testuser3", "password");
        String query = "test";
        
        var results1 = contactService.searchContacts(query, user);
        
        var cache = cacheManager.getCache("searches");
        assertNotNull(cache);
        assertNotNull(cache.get("user:" + user.getId() + ":query:" + query));
        
        var results2 = contactService.searchContacts(query, user);
        
        assertEquals(results1.size(), results2.size());
    }
}