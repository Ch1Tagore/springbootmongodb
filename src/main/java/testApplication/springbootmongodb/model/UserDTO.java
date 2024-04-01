package testApplication.springbootmongodb.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user_account")
public class UserDTO{
    @Id
    String id;
    String username;
    String password;
    String role;

 

}
