package app.state;

public enum StateType {
    MAIN,       // menu principale
    PRIMARY,    // pagine principali sotto il MAIN
    DETAIL,    // Dettagli (senza Main, back torna a Primary)
    SECONDARY,  // pagine secondarie
    AUTH_FLOW   // login / sign-in
}