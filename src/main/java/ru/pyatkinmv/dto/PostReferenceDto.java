package ru.pyatkinmv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostReferenceDto {
    private Integer id;
    private Integer ownerId;
    private Integer date;
}
