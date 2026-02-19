package com.example.amit.repository;

import com.example.amit.models.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@NullMarked
public interface UserRepository extends JpaRepository<User, UUID> {

//    @Query("SELECT DISTINCT e  FROM User e  LEFT JOIN FETCH e.createdBy cb LEFT JOIN FETCH e.updatedBy ub WHERE e.username = :username")
//    Optional<User> findUserByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findAllByIdIn(Set<UUID> ids);

    @Query("SELECT DISTINCT e FROM User e LEFT JOIN FETCH e.createdBy cb LEFT JOIN FETCH e.updatedBy ub WHERE e.email = :email")
    Optional<User> findUserByEmail(String email);
}
