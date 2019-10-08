package com.db;

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

/**
 * Azure Functions with HTTP Trigger.
 */
public class RestAPI {

    @FunctionName("CreateToDo")
    public HttpResponseMessage CreateToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo") 
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context,
            @TableOutput(name = "todos", tableName = "todosTab", partitionKey = "todos", connection = "AzureWebJobsStorage") 
            OutputBinding<ToDoEntity> todoTable) {

        context.getLogger().info("HTTP Get trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String json = request.getBody().orElse(query);

        Gson gson = new Gson();
        ToDo fromJson = gson.fromJson(json, ToDo.class);

        context.getLogger().info("Json received: \n" + fromJson);

        todoTable.setValue(fromJson.toTableEntity());

        return request.createResponseBuilder(HttpStatus.OK).build();
    }

    @FunctionName("GetToDo")
    public HttpResponseMessage GetToDo(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo") 
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context,
            @TableInput(name = "getItems", tableName = "todosTab", partitionKey = "todos", connection = "AzureWebJobsStorage") 
            ToDoEntity[] todosList)
            throws InvalidKeyException, RuntimeException, URISyntaxException, StorageException {

        context.getLogger().info("HTTP Get trigger processed a request.");

        // CloudTableClient tableClient = TableStorageClientProvider.getTableClientReference();
        // CloudTable table = tableClient.getTableReference("todos");

        // List<ToDoEntity> todoList =  partitionScan(table, "todos");

        // ToDoEntity todo1 = table.execute(TableOperation.retrieve("todos", ,ToDoEntity.class)).getResultAsType();

        // Parse query parameter
        String json = request.getBody().orElse("empty body");

        context.getLogger().info("Json received: \n" + json);

        return request.createResponseBuilder(HttpStatus.OK).body(todosList).build();
    }

    @FunctionName("GetToDoById")
    public HttpResponseMessage GetToDoById(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}")
            HttpRequestMessage<String> request,
            final ExecutionContext context, @BindingName("id") int id,
            @TableInput(name = "getItems", tableName = "todosTab", partitionKey = "todos", rowKey = "{id}", 
            connection = "AzureWebJobsStorage") ToDoEntity todos) {

        // Parse query parameter
        context.getLogger().info("Id received: " + id);

        return request.createResponseBuilder(HttpStatus.OK).body(todos).build();
    }

    @FunctionName("UpdateToDoById")
    public HttpResponseMessage UpdateToDoById(
            @HttpTrigger(name = "req", methods = HttpMethod.PUT, authLevel = AuthorizationLevel.ANONYMOUS, route = "todo/{id}") 
            HttpRequestMessage<String> request,
            final ExecutionContext context, @BindingName("id") int id)
            throws StorageException, InvalidKeyException, URISyntaxException {

        CloudTableClient tableClient = TableStorageClientProvider.getTableClientReference();
        CloudTable table = tableClient.getTableReference("todosTab");

        // Parse query parameter
        context.getLogger().info("Id received: " + id);
        context.getLogger().info("CloudTables Name : " + table.getName());

        String json = request.getBody();

        Gson gson = new Gson();
        ToDo fromJson = gson.fromJson(json, ToDo.class);
        context.getLogger().info("Json received: \n" + fromJson);

        // ToDoEntity todo1 = table.execute(TableOperation.retrieve("todos", String.valueOf(id), ToDoEntity.class)).getResultAsType();
        
        
        TableOperation retreiveOperation = TableOperation.retrieve("todos", String.valueOf(id), ToDoEntity.class);

        ToDoEntity updateToDo = table.execute(retreiveOperation).getResultAsType();

        // updateToDo.description = fromJson.getDescription();

        context.getLogger().info("Found Todo : " + updateToDo + "Row Key:  " + updateToDo.getRowKey()  + " Etag: " + updateToDo.getEtag());
        context.getLogger().info("Description: " + updateToDo.getDescription());

        ToDoEntity newToDoEntity = new ToDoEntity(updateToDo.getRowKey());
        newToDoEntity.setEtag(updateToDo.getEtag());       
        newToDoEntity.setDescription(fromJson.getDescription()); 
        table.execute(TableOperation.replace(newToDoEntity));

        // TableOperation insertOrReplaceOperation = TableOperation.InsertOrReplace(updateEntity);
        
        return request.createResponseBuilder(HttpStatus.OK).build();
    }   

    private static List<ToDoEntity> partitionScan(CloudTable table, String partitionKey) throws StorageException {

        // Create the partition scan query
        TableQuery<ToDoEntity> partitionScanQuery = TableQuery.from(ToDoEntity.class).where(
            (TableQuery.generateFilterCondition("PartitionKey", QueryComparisons.EQUAL, partitionKey)));

        List<ToDoEntity> todoEntities = new ArrayList<>();
            
        // Iterate through the results
        // for (ToDoEntity entity : table.execute(partitionScanQuery)) {
        //     System.out.println(String.format("\tCustomer: %s,%s\t%s\t%s", 
        //     entity.getPartitionKey(), entity.getRowKey(), entity.getItems(), entity.getDescription()));
        // }

        table.execute(partitionScanQuery).forEach(s -> todoEntities.add(s));;
        return todoEntities;
    }
}