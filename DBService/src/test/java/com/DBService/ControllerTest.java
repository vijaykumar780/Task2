package com.DBService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.DBService.Entiry.DataTable;
import com.DBService.Entiry.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private DBService dbService;

    @InjectMocks
    private Controller controller;

    private DataTable dataTable;

    private DataTable parent;
    private DataTable child1;
    private DataTable child2;

    @BeforeEach
    void setUp() {
        parent = new DataTable();
        parent.setId(1);
        parent.setName("Parent1");
        parent.setParentId(0);

        child1 = new DataTable();
        child1.setId(2);
        child1.setName("Child1");
        child1.setParentId(1);

        child2 = new DataTable();
        child2.setId(3);
        child2.setName("Child2");
        child2.setParentId(1);

        dataTable = new DataTable();
        dataTable.setId(1);
        dataTable.setParentId(0);
        dataTable.setName("Wizard");
        dataTable.setColor("white");
    }

    @Test
    void testGetAll() {
        List<DataTable> mockData = Arrays.asList(dataTable, new DataTable());
        when(dbService.getAll()).thenReturn(mockData);

        List<DataTable> result = controller.getAll();

        assertEquals(2, result.size());
        verify(dbService, times(1)).getAll();
    }

    @Test
    void testGetById_success() {
        when(dbService.getById(1)).thenReturn(Optional.of(dataTable));

        ResponseEntity<DataTable> response = controller.getById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null);
        assertEquals(1, response.getBody().getId());
        assertEquals(0, response.getBody().getParentId());
        assertEquals("Wizard", response.getBody().getName());
        assertEquals("white", response.getBody().getColor());
        verify(dbService, times(1)).getById(1);
    }

    @Test
    void testGetById_notFound() {
        when(dbService.getById(1)).thenReturn(Optional.empty());

        ResponseEntity<DataTable> response = controller.getById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(dbService, times(1)).getById(1);
    }

    @Test
    void testInsertData_success() {
        ResponseEntity<String> response = controller.GetConcat(dataTable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data saved", response.getBody());
        verify(dbService, times(1)).save(dataTable);
    }

    @Test
    void testInsertData_exceptionHandling() {
        doThrow(new RuntimeException("Database Error")).when(dbService).save(dataTable);

        ResponseEntity<String> response = controller.GetConcat(dataTable);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error occurred in processing request", response.getBody());
        verify(dbService, times(1)).save(dataTable);
    }

    @Test
    void testFetchNestedResponse_success() {
        when(dbService.getAll()).thenReturn(Arrays.asList(parent, child1, child2));

        ResponseEntity<?> response = controller.fetchNestedResponseFromTable();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<Result> resultList = (List<Result>) response.getBody();
        assertEquals(1, resultList.size());
        assertEquals("Parent1", resultList.get(0).getName());
        assertEquals(2, resultList.get(0).getSubClasses().size());
        assertEquals("Child1", resultList.get(0).getSubClasses().get(0).getName());
        assertEquals("Child2", resultList.get(0).getSubClasses().get(1).getName());

        verify(dbService, times(1)).getAll();
    }

    @Test
    void testFetchNestedResponse_emptyData() {
        when(dbService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.fetchNestedResponseFromTable();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((List<Result>) response.getBody()).isEmpty());

        verify(dbService, times(1)).getAll();
    }

    @Test
    void testFetchNestedResponse_exceptionHandling() {
        when(dbService.getAll()).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<?> response = controller.fetchNestedResponseFromTable();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error occurred in processing request", response.getBody());

        verify(dbService, times(1)).getAll();
    }
}