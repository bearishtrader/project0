package services;

import dao.ClientDao;
import model.Client;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {
    ClientDao clientDao = Mockito.mock(ClientDao.class);
    ClientService clientService;
    public ClientServiceTest() { this.clientService = new ClientService(clientDao);}

    @Test
    void createClient() {
        Client client = new Client(1,"John", "Codesmith");
        Mockito.when(clientDao.createClient(client)).thenReturn(true);
        Boolean actualResult = clientService.createClient(client);
        assertTrue(actualResult);
    }

    @Test
    void getAllClients() {
        // arrange
        List<Client> clients = new ArrayList<>();
        clients.add(new Client(1, "Mary", "Codesmith"));
        clients.add(new Client(2, "John", "Smith"));
        clients.add(new Client(3, "Henry", "Ford"));
        List<Client> expectedValue = clients;
        Mockito.when(clientDao.getAllClients()).thenReturn(clients);
        // act(ual)
        List<Client> actualResult = clientService.getAllClients();
        // assert
        assertArrayEquals(expectedValue.toArray(), actualResult.toArray());
    }

    @Test
    void getOneClient() {
        Client expectedResult = new Client(3, "Edward", "Haskel");
        Mockito.when(clientDao.getOneClient(expectedResult.getClientId())).thenReturn(expectedResult);
        Client actualResult = clientService.getOneClient(expectedResult.getClientId());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateClient() {
        List<Client> clients = new ArrayList<>();
        clients.add(new Client(1, "John", "Smith"));
        clients.add(new Client(2, "Joan", "Smith"));
        clients.get(1).setFirstName("Joan2");
        clients.get(1).setLastName("Smith2");
        Mockito.when(clientDao.updateClient(clients.get(1))).thenReturn(true);
        Boolean actualResult = clientService.updateClient(clients.get(1));
        assertTrue(actualResult);
    }

    @Test
    void deleteClient() {
        Mockito.when(clientDao.deleteClient(1)).thenReturn(true);
        Boolean actualResult = clientService.deleteClient(1);
        assertTrue(actualResult);
    }
}