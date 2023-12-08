package com.example.eccentricclothing.securityconfig;


import com.example.eccentricclothing.model.User;
import com.example.eccentricclothing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user=userRepo.findByEmail(email);

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        if(user!=null){
            return new CustomUserDetails(user);
        }

        throw new UsernameNotFoundException("User not available");

    }
}
