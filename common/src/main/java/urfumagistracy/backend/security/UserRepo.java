package urfumagistracy.backend.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import urfumagistracy.backend.User;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String user);

    User findUserByRefresh(String refreshToken);

    boolean existsUserByUsername(String username);
    boolean existsUserByEmail(String email);
}
