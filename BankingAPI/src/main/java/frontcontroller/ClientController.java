package frontcontroller;

import io.javalin.http.Context;
import model.Client;
import org.apache.log4j.Logger;
import services.ClientService;
import util.StatusObj;

import java.util.List;

public class ClientController {
    static ClientService clientService = new ClientService();
    static Logger logger = Logger.getLogger(ClientController.class);
    //POST /clients => Creates a new client return a 201 status code
    public static void createClient(Context context) {
        Client client = context.bodyAsClass(Client.class);
        StatusObj statusObj = new StatusObj();
        if (clientService.createClient(client)) {
            statusObj.status = 201;
            statusObj.message = client.toString()+" created successfully.";
            logger.info(statusObj);
        } else {
            statusObj.status = 400;
            statusObj.message = "An error has occurred, "+client.toString()+" not created try setting clientId to null.";
            logger.error(statusObj);
        }
        context.status(statusObj.status);
        context.json(statusObj);
    }
    //GET /clients => gets all clients return 200
    public static void getAllClients(Context context) {
        List<Client> clients = clientService.getAllClients();
        StatusObj statusObj = new StatusObj();
        if (clients.size()>0) {
            statusObj.status = 200;
            context.status(statusObj.status);
            statusObj.message = "Successfully retrieved list of all clients "+clients;
            logger.info(statusObj);
            context.json(clients);
        }
        else {
            statusObj.status = 404;
            statusObj.message = "No clients found.";
            logger.error(statusObj);
            context.status(statusObj.status);
            context.json(statusObj);
        }
    }
    // GET /clients/10 => get client with id of 10 return 404 if no such client exist
    public static void getOneClient(Context context) {
        Integer clientId = Integer.parseInt(context.pathParam("client_id"));
        Client client = clientService.getOneClient(clientId);
        StatusObj statusObj = new StatusObj();
        if (client != null) {
            statusObj.status = 200;
            statusObj.message = "Client "+client+" successfully retrieved.";
            logger.info(statusObj);
            context.status(statusObj.status);
            context.json(client);
        }
        else {
            statusObj.status = 404;
            statusObj.message = "Client with clientId="+clientId+" not found.";
            logger.error(statusObj);
            context.status(statusObj.status);
            context.json(statusObj);
        }
    }
    //PUT /clients/12 => updates client with id of 12 return 404 if no such client exist
    public static void updateClient(Context context) {
        Client client = context.bodyAsClass(Client.class);
        StatusObj statusObj = new StatusObj();
        if (client!=null) {
            client.setClientId(Integer.parseInt(context.pathParam("client_id")));
            if (clientService.updateClient(client)) {
                statusObj.status = 200;
                statusObj.message = "Client " + client.getClientId() + " successfully updated.";
                logger.info(statusObj);
            } else {
                statusObj.status = 404;
                statusObj.message = "Update unsuccessful.  Client with clientId=" + client.getClientId()+" not found.";
                logger.error(statusObj);
            }
            context.status(statusObj.status);
            context.json(statusObj);
        } else {
            statusObj.status = 400;
            statusObj.message = "Body of request has improperly formatted JSON string.";
            logger.error(statusObj);
        }
        context.status(statusObj.status);
        context.json(statusObj);
    }
    //DELETE /clients/15 => deletes client with the id of 15 return 404 if no such client exist return 205 if success
    public static void deleteClient(Context context) {
        Integer clientId = Integer.parseInt(context.pathParam("client_id"));
        StatusObj statusObj = new StatusObj();
        if (clientService.deleteClient(clientId)) {
            statusObj.status = 205;
            statusObj.message = "Client with clientId="+clientId+ " successfully deleted.";
            logger.info(statusObj);
        }
        else {
            statusObj.status = 404;
            statusObj.message = "Delete unsuccessful.  Client with clientId="+clientId+ " not found.";
            logger.error(statusObj);
        }
        context.status(statusObj.status);
        context.json(statusObj);
    }
}
