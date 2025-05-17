package com.example.bookadv.DTO;

import jakarta.validation.constraints.NotBlank;
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
    private String newPassword;

    @NotBlank(message = "Debe confirmar la nueva contraseña.")
    private String confirmPassword;

    
}
