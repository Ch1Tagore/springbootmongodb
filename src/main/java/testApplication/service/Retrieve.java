package testApplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testApplication.springbootmongodb.model.TodoDTO;
import testApplication.springbootmongodb.repository.TodoRepository;

import java.util.List;

@Service
public class Retrieve {

    private final TodoRepository repository;

    @Autowired
    public Retrieve(TodoRepository repository) {
        this.repository = repository;
    }

    public List<TodoDTO> findAll() {
        return repository.findAll();
    }

    public void save(TodoDTO dto) {
        repository.save(dto);
    }
}
