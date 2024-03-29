package org.bohdan.answers.store.repositories;

import org.bohdan.answers.store.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Stream<UserEntity> streamAllBy();

    Optional<UserEntity> findByActivationCode(String code);
}
