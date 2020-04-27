package kafka.chatting.model;

public class User {
    private String name;

    public User() {
        this.name = String.format("USER#%04d", getRandomNumber());
    }

    private int getRandomNumber() {
        return (int) (Math.random() * 1000);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        User user = (User) obj;
        return this.getName().equals(user.getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
