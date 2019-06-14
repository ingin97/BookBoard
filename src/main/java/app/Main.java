package app;

public class Main {
    /*
     * Klassen som kjører automagisk når maven builder.
     * Kjører app.FXMLMain.main metoden.
     */
    public static void main(String[] args) {
        FXMLMain.main(args);
        //app.UserManager.main();
    }

    public static int add(int x, int y) {
        return x + y;
    }


}
