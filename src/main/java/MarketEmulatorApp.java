import quickfix.Application;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.SessionID;

public class MarketEmulatorApp extends MessageCracker implements Application {
    @Override
    public void onCreate(SessionID s) {
    }

    @Override
    public void onLogon(SessionID s) {
        System.out.println("Market: Logon from Mux!");
    }

    @Override
    public void onLogout(SessionID s) {
    }

    @Override
    public void toAdmin(Message m, SessionID s) {
    }

    @Override
    public void fromAdmin(Message m, SessionID s) {
    }

    @Override
    public void toApp(Message m, SessionID s) {
    }

    @Override
    public void fromApp(Message m, SessionID s) {
        System.out.println("Market received: " + m);
    }
}