package testApplication.springbootmongodb.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import testApplication.springbootmongodb.model.TodoDTO;

@Repository 
public interface TodoRepository extends MongoRepository<TodoDTO,String>{
          
}
