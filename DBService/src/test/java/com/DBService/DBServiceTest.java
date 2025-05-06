package com.DBService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.DBService.Entiry.DataTable;
import com.DBService.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DBServiceTest {

    @Mock
    private Repository repository;

    @InjectMocks
    private DBService dbService;

    private DataTable dataTable;

    @BeforeEach
    void setUp() {
        dataTable = new DataTable();
        dataTable.setId(1);
        dataTable.setParentId(0);
        dataTable.setName("Wizard");
        dataTable.setColor("white");
    }

    @Test
    void testGetById_success() {
        when(repository.findById(1)).thenReturn(Optional.of(dataTable));

        Optional<DataTable> result = dbService.getById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals(0, result.get().getParentId());
        assertEquals("Wizard", result.get().getName());
        assertEquals("white", result.get().getColor());

        verify(repository, times(1)).findById(1);
    }

    @Test
    void testGetById_notFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Optional<DataTable> result = dbService.getById(1);

        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(1);
    }

    @Test
    void testGetAll() {
        List<DataTable> mockData = Arrays.asList(dataTable, new DataTable());
        when(repository.findAll()).thenReturn(mockData);

        List<DataTable> result = dbService.getAll();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testSave() {
        dbService.save(dataTable);

        verify(repository, times(1)).save(dataTable);
    }
}