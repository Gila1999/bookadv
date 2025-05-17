package com.example.bookadv.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {
    
    @NotBlank(message = "La contraseña actual es obligatoria.")
    private String currentPassword;

    @NotBlank(message = "La contraseña nueva es obligatoria.")
    @Size(min = 4, message = "La confirmación debe tener al menos 4 caracteres")
    private String newPassword;

    @NotBlank(message = "Debe confirmar la nueva contraseña.")
    @Size(min = 4, message = "La confirmación debe tener al menos 4 caracteres")
    private String confirmPassword;

    
}
