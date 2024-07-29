package tech.aurasoftware.aurautilitiesplus.util;

import java.util.List;


public class Placeholder {

    private String key, value;

    private List<String> values;

    public Placeholder(String key, String value){
        this.key = key;
        this.value = value;
    }

    public Placeholder(String key, List<String> values){
        this.key = key;
        this.values = values;
    }

    public String apply(String message){

        if(value != null) {
            return message.replace(key, value);
        }

        if(!message.contains(key)){
            return message;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(String value : values) {
            stringBuilder.append(message.replace(key, value)).append(System.lineSeparator());
        }

        System.out.println(stringBuilder.toString());

        return stringBuilder.toString();
    }

    public static String apply(String message, Placeholder... placeholders){
        for(Placeholder placeholder : placeholders) {
            message = placeholder.apply(message);
        }

        return message;
    }

}
