package tech.aurasoftware.aurautilitiesplus.message;


import lombok.Getter;
import tech.aurasoftware.aurautilitiesplus.configuration.serialization.Serializable;

@Getter
public class TitleMessage implements Serializable {

    private String header;
    private String footer;
    private int duration = 20;

    public TitleMessage setHeader(String header) {
        this.header = header;
        return this;
    }

    public TitleMessage setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public TitleMessage setDuration(int duration) {
        this.duration = duration;
        return this;
    }

}
