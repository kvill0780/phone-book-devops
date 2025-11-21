package bf.kvill.spring_phone_book.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contact_groups")
@Data
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du groupe est obligatoire")
    @Column(unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Contact> contacts = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
