package ru.pyatkinmv.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.pyatkinmv.dao.entities.FileInfo;

public interface FileRepository extends CrudRepository<FileInfo, Integer> {
    @Query(value = "SELECT data FROM file f WHERE f.id = :id", nativeQuery = true)
    byte[] getFileData(@Param("id") Integer fileId);

    @Modifying
    @Query(value = "UPDATE file SET data = :data WHERE id = :id", nativeQuery = true)
    void saveFileData(@Param("id") Integer fileId, @Param("data") byte[] data);
}
