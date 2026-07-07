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
public class SecurityTokenSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String token;
    private Long userId;
    private Long tenantId;
    private Integer userType;
    private boolean superAdmin;
    private List<String> permissions;
    private List<String> roles;
    private long createdAtEpochSecond;
    private long lastActiveAtEpochSecond;
    private long expireAtEpochSecond;
}
