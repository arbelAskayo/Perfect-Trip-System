package boundry;

public interface UserStatusObservable {
    void addObserver(UserStatusObserver observer);
    void removeObserver(UserStatusObserver observer);
    void notifyObservers();
}