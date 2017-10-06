package novels;

public class Choice {
    public String value;
    public String to;

    public Choice (String value, String to) {
        this.value = value;
        this.to = to;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "value='" + value + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

    public boolean match (String answer) {
        return value.equalsIgnoreCase(answer)
                || value.equalsIgnoreCase("other");
    }
}
