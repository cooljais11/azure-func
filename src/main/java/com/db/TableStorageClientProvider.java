package com.db;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;

class TableStorageClientProvider {


    static CloudTableClient getTableClientReference() throws RuntimeException, URISyntaxException, InvalidKeyException {
        CloudStorageAccount storageAccount;
        try {
            String connectionString = "DefaultEndpointsProtocol=https;AccountName=dicefunc;AccountKey=79MWuAA/WkCcEiFZOJd1qLWZLJ6GI0xWEEV0MMH4JLbhjBP50Rb6hqRTNVEWGEvj0Y3Jxeb9QjJL7n9kV09npg==;EndpointSuffix=core.windows.net";
            storageAccount = CloudStorageAccount.parse(connectionString);

        } catch (IllegalArgumentException | URISyntaxException e) {
            System.out.println("\nConnection string specifies an invalid URI.");
            System.out.println("Please confirm the connection string is in the Azure connection string format.");
            throw e;
        } catch (InvalidKeyException e) {
            System.out.println("\nConnection string specifies an invalid key.");
            System.out.println("Please confirm the AccountName and AccountKey in the connection string are valid.");
            throw e;
        }

        return storageAccount.createCloudTableClient();
    }
}