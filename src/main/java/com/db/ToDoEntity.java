package com.db;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class ToDoEntity extends TableServiceEntity {

    public int items;
    public String description;

    public ToDoEntity() {
    }

    public ToDoEntity(String rowKey) {
        this.partitionKey = "todos";
        this.rowKey = rowKey;
    }    

    public ToDoEntity(int items, String description) {
        this.partitionKey = "todos";
        this.rowKey = String.valueOf(items);
        this.items = items;
        this.description = description;
    }

    public int getItems() {
        return this.items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}