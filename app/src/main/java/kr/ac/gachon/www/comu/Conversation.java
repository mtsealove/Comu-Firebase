package kr.ac.gachon.www.comu;

public class Conversation {
    String character;
    String comment;
    String code;

    Conversation(String character, String comment, String code) {
        this.character=character;
        this.comment=comment;
        this.code=code;
    }

    Conversation(String character, String comment) {
        this(character, comment, null);
    }

}
