package ru.pyatkinmv.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pyatkinmv.dao.entities.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
}
