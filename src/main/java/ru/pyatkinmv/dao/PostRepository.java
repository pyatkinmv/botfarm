package ru.pyatkinmv.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.pyatkinmv.dao.entities.Post;

import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query(value = "SELECT * FROM post " +
            "WHERE is_posted = FALSE " +
            "AND type = :type " +
            "AND profile_id = :profile_id " +
            "ORDER BY source_date ASC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Post> findByProfileIdAndType(@Param("profile_id") Integer profileId, @Param("type") String type);
}
