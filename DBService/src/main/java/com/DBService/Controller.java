package com.DBService;

import com.DBService.Entiry.DataTable;
import com.DBService.Entiry.Result;
import com.DBService.Entiry.SubClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/datatable")
public class Controller {

    @Autowired
    private DBService dbService;

    @GetMapping("/fetchAll")
    public List<DataTable> getAll() {
        log.info("Returning all data from table");
        return dbService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataTable> getById(@PathVariable int id) {
        log.info("Fetching data for id: {}", id);
        return dbService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/insertData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> GetConcat(@RequestBody DataTable request) {
        // request vo has name and surname in it
        log.info("Request received {}", request);
        try {
            dbService.save(request);
            return ResponseEntity.ok("Data saved");
        } catch (Exception e) {
            log.error("Error processing request {}", request);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred in processing request");
        }

    }

    @RequestMapping(value = "/fetchNestedResponseFromTable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fetchNestedResponseFromTable() {
        // request vo has name and surname in it
        log.info("Request received for fetchNestedResponseFromTable");
        try {
            List<DataTable> dataList = dbService.getAll();
            List<Result> resultsList = new ArrayList<>();
            Map<String, List<String>> parentToChildren = new HashMap<>();
            Map<Integer, String> parentId = new HashMap<>();

            for (DataTable dataTable : dataList) {
                if (dataTable.getParentId() == 0) {
                    parentId.put(dataTable.getId(), dataTable.getName());
                    List<String> children = parentToChildren.get(dataTable.getName());
                    if (children == null) {
                        // init parent to children list
                        parentToChildren.put(dataTable.getName(), new ArrayList<>());
                    }
                }
            }

            for (DataTable dataTable : dataList) {
                if (dataTable.getParentId() != 0) {
                    List<String> children = parentToChildren.get(parentId.get(dataTable.getParentId()));
                    children.add(dataTable.getName());
                }
            }

            // Parent child mappings
            for (Map.Entry<String, List<String>> entry : parentToChildren.entrySet()) {
                List<String> children = entry.getValue();
                String name = entry.getKey();
                List<SubClass> subClasses = new ArrayList<>();
                for (String child : children) {
                    subClasses.add(SubClass.builder().Name(child).build());
                }
                resultsList.add(Result.builder().Name(name).SubClasses(subClasses).build());
            }

            return ResponseEntity.status(HttpStatus.OK).body(resultsList);
        } catch (Exception e) {
            log.error("Error processing request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred in processing request");
        }

    }
}
