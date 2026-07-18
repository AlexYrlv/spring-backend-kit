package io.github.alexyrlv.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data generates the implementation. Package-private:
 * only {@link UserStorage} is allowed to touch entities.
 */
interface UserJpaRepository extends JpaRepository<UserEntity, String> {
}
