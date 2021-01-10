package ru.pyatkinmv.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.pyatkinmv.dao.entities.Post;
import ru.pyatkinmv.dao.entities.PostType;

import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query(value = "SELECT * FROM post " +
            "WHERE supplied = FALSE " +
            "AND type = :type " +
            "ORDER BY source_date ASC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Post> findPostByType(@Param("type") String type);
}
