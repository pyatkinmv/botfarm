package ru.pyatkinmv.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pyatkinmv.dao.entities.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    Optional<Profile> findByUserId(Integer userId);
}
