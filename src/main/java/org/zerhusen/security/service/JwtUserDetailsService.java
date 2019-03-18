package org.zerhusen.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerhusen.model.security.User;
import org.zerhusen.security.JwtUserFactory;
import org.zerhusen.security.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mobilenumber) throws UsernameNotFoundException {
        User user = userRepository.findByMobilenumber(mobilenumber);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with mobilenumber '%s'.", mobilenumber));
        } else {
            return JwtUserFactory.create(user);
        }
    }
}
