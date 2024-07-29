package tech.aurasoftware.aurautilitiesplus.command.parameter.implementation;


import tech.aurasoftware.aurautilitiesplus.command.parameter.Parameter;

import java.util.Arrays;
import java.util.List;

public class DoubleParameter extends Parameter<Double> {

    public DoubleParameter() {
        super(Double.class);
    }

    @Override
    public Double parse(String input) {
        try{
            double x = Double.parseDouble(input);

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
        return Arrays.asList("1.0", "2.0", "4.0", "8.0", "16.0", "32.0", "64.0");
    }



}
