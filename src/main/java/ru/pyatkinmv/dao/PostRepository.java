package ru.pyatkinmv.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.pyatkinmv.dao.entities.Post;

import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query(value = "SELECT * FROM post " +
            "WHERE posted = FALSE " +
            "AND profile_id = :profile_id " +
            "ORDER BY source_date ASC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Post> findOldestNotPostedById(@Param("profile_id") Integer profileId);

    @Query(value = "SELECT p FROM post p INNER JOIN file f ON p.id = f.post_id " +
            "WHERE posted = FALSE " +
            "AND profile_id = :profile_id " +
            "GROUP BY p.id " +
            "HAVING COUNT(f.id) = 1 " +
            "ORDER BY likes_count DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Post> findMostLikedNotPostedById(@Param("profile_id") Integer profileId);
}
