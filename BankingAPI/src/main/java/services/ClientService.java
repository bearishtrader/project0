package services;

import dao.ClientDao;
import dao.ClientDaoImpl;
import model.Client;

import java.util.List;

public class ClientService {
    ClientDao clientDao;

    public ClientService() {
        clientDao = new ClientDaoImpl();
    }
    public ClientService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }
    public Boolean createClient(Client client) { return clientDao.createClient(client);}
    public List<Client> getAllClients() {
        return clientDao.getAllClients();
    }
    public Client getOneClient(Integer clientId) {
        return clientDao.getOneClient(clientId);
    }
    public Boolean updateClient(Client client) {
        return clientDao.updateClient(client);
    }
    public Boolean deleteClient(Integer clientId) { return clientDao.deleteClient(clientId); }
}
