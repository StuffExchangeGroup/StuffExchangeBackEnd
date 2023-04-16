package pbm.com.exchange.message;

public class Message {

    private Keys id;
    private String content;

    @Override
    public String toString() {
        return content;
    }

    public enum Keys {
        I0001,
        I0002,
        I0003,
        I0004,
        E0003,
        E0004,
        E0005,
        E0011,
        E0012,
        E0013,
        E0024,
        E0044,
        E0063,
        E0064,
        E0065,
        E0066,
        E0067,
        E0068,
        E0069,
        E0070,
        E0071,
        E0072,
        E0073,
        E0074,
        E0075,
        E0076,
        E0077,
        E0078,
        E0079,
        E0080,
        E0081,
        E0082,
        E0083,
        E0084,
        E0085,
        E0086,
        E0087,
        E0088,
        E0089,
        E0090,
        E0091,
        E0092,
        E0093,
        E0094,
        E0095,
        E0096,
        E0097,
        E0098,
        E0099,
        E0100,
    }

    public Keys getId() {
        return id;
    }

    public void setId(Keys id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
