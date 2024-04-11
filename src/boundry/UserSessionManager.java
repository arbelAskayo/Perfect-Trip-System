package boundry;

import java.util.ArrayList;
import java.util.List;

import entity.Member;

public class UserSessionManager implements UserStatusObservable {
    private boolean isRegisteredUser;
    private Long currentUser; // The currently logged-in user

    private final List<UserStatusObserver> observers = new ArrayList<>();
    private static final UserSessionManager instance = new UserSessionManager();

    private UserSessionManager() {}

    public static UserSessionManager getInstance() {
        return instance;
    }

    @Override
    public void addObserver(UserStatusObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(UserStatusObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (UserStatusObserver observer : observers) {
            observer.update(isRegisteredUser);
        }
    }

    public void setRegisteredUser(boolean isRegisteredUser) {
        this.isRegisteredUser = isRegisteredUser;
        notifyObservers();
    }

    public Long getCurrentUser() {
        return currentUser;
    }
    
    public void login(Long user) {
    	setRegisteredUser(true);
        this.currentUser = user; // Set the currently logged-in user
    }

    public boolean isRegisteredUser() {
        return isRegisteredUser;
    }
    
    public void logout() {
        this.isRegisteredUser = false;
        this.currentUser = null; // Clear the current user upon logout
    }

    public void switchToLimitedAccess() {
        setRegisteredUser(false);
        this.currentUser = (long) 1;
    }
}