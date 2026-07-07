package me.link.bootstrap.infrastructure.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserPrincipal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long tenantId;
    private Integer userType;
    private boolean superAdmin;
    private List<String> permissions;
    private List<String> roles;
}
