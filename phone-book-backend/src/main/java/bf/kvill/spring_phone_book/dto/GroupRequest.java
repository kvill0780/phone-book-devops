package bf.kvill.spring_phone_book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupRequest {
    
    @NotBlank(message = "Le nom du groupe est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom du groupe doit contenir entre 2 et 50 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\s'-]+$", message = "Le nom du groupe ne peut contenir que des lettres, chiffres, espaces, apostrophes et tirets")
    private String name;
    
    @Size(max = 200, message = "La description ne peut pas dépasser 200 caractères")
    private String description;
}