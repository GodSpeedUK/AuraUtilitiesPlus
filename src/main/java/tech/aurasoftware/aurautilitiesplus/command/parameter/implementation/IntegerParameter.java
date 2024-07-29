package tech.aurasoftware.aurautilitiesplus.command.parameter.implementation;


import tech.aurasoftware.aurautilitiesplus.command.parameter.Parameter;

import java.util.Arrays;
import java.util.List;

public class IntegerParameter extends Parameter<Integer> {
    public IntegerParameter() {
        super(Integer.class);
    }

    @Override
    public Integer parse(String input) {
        try{
            int x = Integer.parseInt(input);

            if(x < 0){
                return null;
            }
            return x;
        }catch (NumberFormatException e) {
            return null;
        }

    }

    @Override
    public List<String> tabComplete() {
        return Arrays.asList("1", "2", "4", "8", "16", "32", "64");
    }

}
