package dao;

import model.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.ConnectionUtil;
import util.H2Util;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientDaoImplTest {

    ClientDao clientDao;

    public ClientDaoImplTest() {
        ConnectionUtil.setConnectionParams(H2Util.url, H2Util.username, H2Util.password);
        clientDao = new ClientDaoImpl();
    }
    @BeforeEach
    void setUp() {
        H2Util.createClientsTable();
        H2Util.createAccountsTable();
    }

    @AfterEach
    void tearDown() {
        H2Util.dropAccountsTable();
        H2Util.dropClientsTable();
    }

    @Test
    void createClientIT() {
        // arrange
        List<Client> expectedResult = new ArrayList<>();
        expectedResult.add(new Client(1, "Bob", "Evans"));
        expectedResult.add(new Client(2, "Tom", "Jones"));
        expectedResult.add(new Client(3, "Jenny", "Codesmith"));
        // act
        clientDao.createClient(expectedResult.get(0));
        clientDao.createClient(expectedResult.get(1));
        clientDao.createClient(expectedResult.get(2));
        // assert
        List<Client> actualResult = clientDao.getAllClients();
        System.out.println("expectedResult="+expectedResult);
        System.out.println("actualResult="+actualResult);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getAllClientsIT() {
        // arrange
        List<Client> expectedResult = new ArrayList<>();
        expectedResult.add(new Client(1, "Bob", "Evans"));
        expectedResult.add(new Client(2, "Tom", "Jones"));
        expectedResult.add(new Client(3, "Jenny", "Codesmith"));
        // act
        clientDao.createClient(expectedResult.get(0));
        clientDao.createClient(expectedResult.get(1));
        clientDao.createClient(expectedResult.get(2));
        // assert
        List<Client> actualResult = clientDao.getAllClients();
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getAllClientsInvalidNoClientsIT() {
        // arrange
        List<Client> expectedResult = new ArrayList<>();

        // act

        // assert
        List<Client> actualResult = clientDao.getAllClients();
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getOneClientIT() {
        // arrange
        List<Client> clientList = new ArrayList<>();
        clientList.add(new Client(1, "Bob", "Evans"));
        clientList.add(new Client(2, "Tom", "Jones"));
        clientList.add(new Client(3, "Jenny", "Codesmith"));
        clientList.add(new Client(4, "Jeremy", "Lin"));
        // act
        clientDao.createClient(clientList.get(0));
        clientDao.createClient(clientList.get(1));
        clientDao.createClient(clientList.get(2));  // <- the expected result Jenny Codesmith or client 3
        clientDao.createClient(clientList.get(3));
        Client oneClient = clientDao.getOneClient(3);
        assertEquals(clientList.get(2), oneClient);
    }

    @Test
    void getOneClientInvalidNoClientFoundIT() {
        // arrange
        List<Client> clientList = new ArrayList<>();
        clientList.add(new Client(1, "Bob", "Evans"));
        clientList.add(new Client(2, "Tom", "Jones"));
        clientList.add(new Client(3, "Jenny", "Codesmith"));
        clientList.add(new Client(4, "Jeremy", "Lin"));
        // act
        clientDao.createClient(clientList.get(0));
        clientDao.createClient(clientList.get(1));
        clientDao.createClient(clientList.get(2));  // <- the expected result Jenny Codesmith or client 3
        clientDao.createClient(clientList.get(3));
        Client oneClient = clientDao.getOneClient(5);
        assertNull(oneClient);
    }

    @Test
    void updateClientIT() {
        // arrange
        List<Client> clientList = new ArrayList<>();
        clientList.add(new Client(1, "Bob", "Evans"));
        clientList.add(new Client(2, "Tom", "Jones"));
        clientList.add(new Client(3, "Jenny", "Codesmith"));
        clientList.add(new Client(4, "Jeremy", "Lin"));
        // act
        clientDao.createClient(clientList.get(0));
        clientDao.createClient(clientList.get(1));
        clientDao.createClient(clientList.get(2));
        clientDao.createClient(clientList.get(3)); //<- update client 4 Jeremy Lin
        Client expectedResult = clientList.get(3);
        expectedResult.setFirstName("Jeremy2");
        expectedResult.setLastName("Lin2");
        Client oneClient = (clientDao.updateClient(expectedResult)==true) ? clientDao.getOneClient(4): null;
        assertEquals(expectedResult, oneClient);
    }

    @Test
    void updateClientInvalidClientNotFoundIT() {
        // arrange
        List<Client> clientList = new ArrayList<>();
        clientList.add(new Client(1, "Bob", "Evans"));
        clientList.add(new Client(2, "Tom", "Jones"));
        clientList.add(new Client(3, "Jenny", "Codesmith"));
        clientList.add(new Client(4, "Jeremy", "Lin"));
        // act
        clientDao.createClient(clientList.get(0));
        clientDao.createClient(clientList.get(1));
        clientDao.createClient(clientList.get(2));
        clientDao.createClient(clientList.get(3));
        Client updateClient = clientList.get(3);
        updateClient.setClientId(5);
        updateClient.setFirstName("Jeremy2");
        updateClient.setLastName("Lin2");
        Client oneClient = (clientDao.updateClient(updateClient)==true) ? clientDao.getOneClient(5): null;
        assertNull(oneClient);
    }

    @Test
    void deleteClientIT() {
        // arrange
        List<Client> expectedResult = new ArrayList<>();
        expectedResult.add(new Client(1, "Bob", "Evans"));
        expectedResult.add(new Client(2, "Tom", "Jones"));
        expectedResult.add(new Client(3, "Jenny", "Codesmith"));
        expectedResult.add(new Client(4, "Jeremy", "Lin"));
        // act
        clientDao.createClient(expectedResult.get(0));
        clientDao.createClient(expectedResult.get(1)); //<- delete clientId=2 Tom Jones
        clientDao.createClient(expectedResult.get(2));
        clientDao.createClient(expectedResult.get(3));

        List<Client> actualResult = new ArrayList<>();
        if ( clientDao.deleteClient(expectedResult.get(1).getClientId()) ) {
            actualResult = clientDao.getAllClients();
        }
        expectedResult.remove(1); // <- delete clientId=2 Tom Jones

        // assert
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void deleteClientInvalidNotFoundIT() {
        // arrange
        List<Client> expectedResult = new ArrayList<>();
        expectedResult.add(new Client(1, "Bob", "Evans"));
        expectedResult.add(new Client(2, "Tom", "Jones"));
        expectedResult.add(new Client(3, "Jenny", "Codesmith"));
        expectedResult.add(new Client(4, "Jeremy", "Lin"));
        // act
        clientDao.createClient(expectedResult.get(0));
        clientDao.createClient(expectedResult.get(1)); //<- delete clientId=2 Tom Jones
        clientDao.createClient(expectedResult.get(2));
        clientDao.createClient(expectedResult.get(3));

        List<Client> actualResult = new ArrayList<>();
        clientDao.deleteClient(5);
        actualResult = clientDao.getAllClients();

        // assert
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());    // no change
    }
}