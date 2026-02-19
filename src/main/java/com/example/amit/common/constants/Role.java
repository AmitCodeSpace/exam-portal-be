package com.example.amit.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.amit.common.constants.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
//  USER(Collections.emptySet()),
//  ADMIN(Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE, ADMIN_CREATE, MANAGER_READ, MANAGER_UPDATE, MANAGER_DELETE, MANAGER_CREATE)),
//  MANAGER(Set.of(MANAGER_READ, MANAGER_UPDATE, MANAGER_DELETE, MANAGER_CREATE)),

  SYSTEM_ADMIN(Set.of(
          SYSTEM_ADMIN_READ,
          SUPER_ADMIN_READ,
          ADMIN_READ,
          USER_READ
  )),

  SUPER_ADMIN(Set.of(
          SUPER_ADMIN_READ, SUPER_ADMIN_UPDATE, SUPER_ADMIN_CREATE, SUPER_ADMIN_DELETE,
          ADMIN_READ, ADMIN_UPDATE, ADMIN_CREATE, ADMIN_DELETE,
          USER_READ, USER_UPDATE, USER_CREATE, USER_DELETE
  )),

  ADMIN(Set.of(
          ADMIN_READ, ADMIN_UPDATE,
          USER_READ, USER_UPDATE, USER_CREATE, USER_DELETE
  )),

//  MANAGER(Set.of(USER_READ, USER_UPDATE,  USER_CREATE)),

//  ADMIN_MANAGER(Set.of(ADMIN_MANAGER_UPDATE, DEVELOPER_CREATE, USER_READ, USER_UPDATE,  USER_CREATE, USER_DELETE)),
//
//  DEVELOPER_MANAGER(Set.of(DEVELOPER_READ, DEVELOPER_UPDATE, USER_READ, USER_UPDATE,  USER_CREATE, USER_DELETE)),
//
//  TECHNICAL_MANAGER(Set.of(TECHNICAL_READ, TECHNICAL_UPDATE, USER_READ, USER_UPDATE,  USER_CREATE, USER_DELETE)),
//
//  ACCOUNTS_MANAGER(Set.of(ACCOUNTS_READ, ACCOUNTS_UPDATE, USER_READ, USER_UPDATE,  USER_CREATE, USER_DELETE)),
//
//  SALES_MANAGER(Set.of(SALES_READ, SALES_UPDATE, USER_READ, USER_UPDATE,  USER_CREATE, USER_DELETE)),
//
//  HR_MANAGER(Set.of(HR_READ, HR_UPDATE, USER_READ, USER_UPDATE, USER_CREATE, USER_DELETE)),

  USER(Set.of(USER_READ, USER_UPDATE));

  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }
}
