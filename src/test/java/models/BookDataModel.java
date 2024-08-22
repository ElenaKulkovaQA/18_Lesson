package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hamcrest.core.Is;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookDataModel {
    private String userId;
    private List<IsbnData> collectionOfIsbns;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IsbnData{
        String isbn;
//        public IsbnData(String isbn){
//            this.isbn = isbn;
//        }
    }
}
