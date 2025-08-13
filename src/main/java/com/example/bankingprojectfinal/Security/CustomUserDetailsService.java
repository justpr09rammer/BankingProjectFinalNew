package com.example.bankingprojectfinal.Security;

import com.example.bankingprojectfinal.Model.Entity.UserEntity;
import com.example.bankingprojectfinal.Model.Enums.UserRole;
import com.example.bankingprojectfinal.Model.Enums.UserStatus;
import com.example.bankingprojectfinal.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        boolean enabled = entity.getStatus() == UserStatus.ACTIVE;
        Collection<? extends GrantedAuthority> authorities = mapAuthorities(entity.getRole());
        return User.withUsername(entity.getUsername())
                .password(entity.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(entity.getStatus() == UserStatus.DISABLED)
                .credentialsExpired(false)
                .disabled(!enabled)
                .build();
    }

    private List<GrantedAuthority> mapAuthorities(UserRole role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}