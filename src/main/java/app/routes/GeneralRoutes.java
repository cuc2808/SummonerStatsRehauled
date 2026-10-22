package app.routes;

public class GeneralRoutes {

    //ROUTES
    /*
    - (ANYONE) Search Account : Searches for account
    Returns a Json with the account with AccountOverview + MatchHistory from the DB
    If the account doesn't exist in DB, then it receives it from RiotAPI

    - (ANYONE) Update Account :
    Returns a Json of the account with AccountOverview + MatchHistory with new data from RiotAPI
    Stores the new data in DB

    - (ADMIN) Update Champions : Updates the data on champions in DB
     Returns a Json of all champions
     ** Should have a system that checks the champion pool once every 24 hours.

    - (USER) SCOUT ACCOUNT : Adds a Riot Account to the User's scouting list
    Returns a Json with listed scouted accounts listed with AccountPreviewDTOs. This will later on be used for quick access
    */


}
