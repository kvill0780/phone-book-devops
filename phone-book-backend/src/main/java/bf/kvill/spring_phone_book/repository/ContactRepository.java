package bf.kvill.spring_phone_book.repository;

import bf.kvill.spring_phone_book.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository pour les opérations CRUD sur Contact
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>
{
    //Recherche par numero de telephone
    List<Contact> findByPhoneNumberContaining(String phoneNumber);

    // Recherche par prénom, sans tenir compte de la casse
    List<Contact> findByFirstNameContainingIgnoreCase(String firstName);

    // Recherche par nom, sans tenir compte de la casse
    List<Contact> findByLastNameContainingIgnoreCase(String lastName);

    List<Contact> findByGroupId(Long groupId);

}