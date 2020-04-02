package events;

import com.fasterxml.jackson.databind.JsonNode;

import javax.websocket.Session;

public class SessionEvent {
    private String event;
    private String user;
    private JsonNode data;

    public SessionEvent(String event, String user, JsonNode data) {
        this.event = event;
        this.user = user;
        this.data = data;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    public String getEvent() {
        return event;
    }

    public String getUser() {
        return user;
    }
}
