package com.DBService;

import com.DBService.Entiry.DataTable;
import com.DBService.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DBService {

    @Autowired
    private Repository repository;

    public Optional<DataTable> getById(int id) {
        return repository.findById(id);
    }

    public List<DataTable> getAll() {
        return repository.findAll();
    }

    public void save(DataTable dataTable) {
        repository.save(dataTable);
    }
}
