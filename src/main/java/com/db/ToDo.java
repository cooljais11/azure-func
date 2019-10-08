package com.db;

class ToDo {

    int items;
    String description;

    public ToDo() {
    }

    public ToDo(int items, String description) {
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

    public ToDo items(int items) {
        this.items = items;
        return this;
    }

    public ToDo description(String description) {
        this.description = description;
        return this;
    }

    public ToDoEntity toTableEntity() {
        return new ToDoEntity(this.getItems(), this.getDescription());
    }
}