package app.controllers;

import io.javalin.http.Context;

public interface IAccountController {
    void addAccount(Context ctx); // Runs the API and adds the Account into DB, will return JSON.
    void findAccount(Context ctx); // Searches for Account in DB based on gameName and tagline and returns a JSON, will addAccount if it doesn't exist.
    void updateAccount(Context ctx); // Receives new data on an Account and updates the DB, returns a JSON.
    void trackAccount(Context ctx); // Adds a selected Account to the User's trackedList for easier access, will return JSON of List.
    void stopTrackingAccount(Context ctx); // Removes a selected Account to the User's trackedList, will return JSON.
}
