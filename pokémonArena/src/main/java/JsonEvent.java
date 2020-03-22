import java.util.Map;

public class JsonEvent {
    public String event;
    public Map<String, String> data;

    public JsonEvent(String event, Map<String, String> data) {
        this.event = event;
        this.data = data;
    }
}
