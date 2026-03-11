package booksapi.auth;

import booksapi.model.User;
import booksapi.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepo;

  public UserDetailsServiceImpl(UserRepository userRepo) {
    this.userRepo = userRepo;
  }

  @Override
  @NonNull
  public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    User user = userRepo.findByUsername(username).orElseThrow(() ->
            new UsernameNotFoundException("No user with that username found."));
    return convertUserToUserDetails(user);
  }

  private UserDetails convertUserToUserDetails(User user) {
    return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities("ROLE_USER")
            .build();
  }
}
