package core;

public abstract class Application extends Window {

    protected void configure(final Configuration config) {
    }

    public static void launch(final Application app) {
        initialize(app);
        app.preProcess();
        app.run();
        app.postProcess();
        app.dispose();
    }

    private static void initialize(final Application app) {
        final Configuration configuration = new Configuration();
        app.configure(configuration);
        app.init(configuration);
    }

    protected void preProcess() {
    }

    protected  void postProcess() {
    }
}
