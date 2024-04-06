package xeonex.gpt.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatgptRequest {

    private String model;
    private List<Message> messages = new ArrayList<>();

    public ChatgptRequest(String model, String query) {
        this.model = model;
        this.messages.add(new Message("user", query));
    }
}
