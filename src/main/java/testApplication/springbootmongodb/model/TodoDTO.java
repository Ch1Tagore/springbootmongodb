package testApplication.springbootmongodb.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
@Data
@AllArgsConstructor
@NoArgsConstructor 
@Document(collection = "Amount")
public class TodoDTO {
    @Id  
    String id;
    float amount;
    String cur_rate;
    String creOrDebt;
    Date dtOfTransDate;

}
