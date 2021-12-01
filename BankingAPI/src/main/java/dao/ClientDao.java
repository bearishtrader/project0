package dao;

import model.Client;

import java.util.List;
// Our DAO (Data Access Object design pattern) interface for CRUD (Create Read Update Delete) operations to abstract the data persistence layer so that if
// JDBC needs to be replaced with something else, the DAO can be simply re-implemented for any new database access APIs
public interface ClientDao {
    Boolean createClient(Client client);
    List<Client> getAllClients();
    Client getOneClient(Integer clientId);
    Boolean updateClient(Client client);
    Boolean deleteClient(Integer clientId);
}
