package ru.pyatkinmv.dao.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}
