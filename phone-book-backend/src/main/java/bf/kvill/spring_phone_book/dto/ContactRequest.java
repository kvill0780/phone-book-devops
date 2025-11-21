package bf.kvill.spring_phone_book.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ContactRequest {
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom ne peut contenir que des lettres, espaces, apostrophes et tirets")
    private String firstName;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom ne peut contenir que des lettres, espaces, apostrophes et tirets")
    private String lastName;
    
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Format de téléphone invalide (format international recommandé)")
    private String phoneNumber;
    
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;
    
    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String address;
    
    @Min(value = 1, message = "L'ID du groupe doit être positif")
    private Long groupId;
}