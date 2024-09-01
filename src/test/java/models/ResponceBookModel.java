package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ResponceBookModel {
    private String userId;
    private String code;
    private String message;
    private List<ResponceBookModel.IsbnData> books;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IsbnData{
        String isbn;
    }
}

