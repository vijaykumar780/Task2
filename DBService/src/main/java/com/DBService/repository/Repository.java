package com.DBService.repository;
import java.util.List;

import com.DBService.Entiry.DataTable;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface Repository extends JpaRepository<DataTable, Integer>{

}