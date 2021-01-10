package ru.pyatkinmv.dao.entities;

import lombok.Data;

import javax.persistence.*;
import java.net.URL;

@Data
@Entity
@Table(name = "file_info")
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String reference;
}
