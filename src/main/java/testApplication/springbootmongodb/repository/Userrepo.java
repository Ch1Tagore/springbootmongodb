package testApplication.springbootmongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import testApplication.springbootmongodb.model.UserDTO;

import java.util.Optional;

@Repository
public interface Userrepo  extends MongoRepository<UserDTO,String > {
    @Query("{user_name:'?0'}")
    Optional<UserDTO>   findByUsername(String username);
}
