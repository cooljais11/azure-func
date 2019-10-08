package com.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;
import com.google.gson.Gson;
import com.microsoft.azure.functions.*;

public class FinalRestAPI {

    protected static CloudTable table = null;
    private static String tableName = "myTodos";

    @FunctionName("CreateToDo1")
    public HttpResponseMessage CreateToDo1(
            @HttpTrigger(name = "req1", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = "todos") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context)
            throws InvalidKeyException, RuntimeException, URISyntaxException, StorageException, IOException {

        context.getLogger().info("HTTP Get trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String json = request.getBody().orElse(query);

        Gson gson = new Gson();
        ToDo fromJson = gson.fromJson(json, ToDo.class);

        context.getLogger().info("Json received: \n" + fromJson);

        CloudTableClient tableClient = TableStorageClientProvider.getTableClientReference();

        table = TableStorageUtilities.createTable(tableClient, tableName);

        ToDoEntity toDoEntity = new ToDoEntity(String.valueOf(fromJson.getItems()));        
        toDoEntity.setDescription(fromJson.getDescription());
        toDoEntity.setItems(fromJson.getItems());
        table.execute(TableOperation.insert(toDoEntity));

        return request.createResponseBuilder(HttpStatus.OK).build();
    }

}