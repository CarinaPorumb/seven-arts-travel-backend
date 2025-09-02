package ro.sevenartstravel.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.sevenartstravel.entity.Role;
import ro.sevenartstravel.entity.User;
import ro.sevenartstravel.service.entity.UserService;

import java.util.*;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {

        User user = userService.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                authorities);
    }

    private List<GrantedAuthority> mapRolesToAuthorities(Set<Role> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) return Collections.emptyList();

        Set<GrantedAuthority> roles = new HashSet<>();

        for (Role role : userRoles) {
            if (role == null || role.getName() == null) continue;

            String name = role.getName().trim().toUpperCase();
            if (name.isEmpty()) continue;

            if (!name.startsWith("ROLE_")){
                name = "ROLE_" + name;
            }

            roles.add(new SimpleGrantedAuthority(name));
        }
        return new ArrayList<>(roles);
    }

}