package tech.aurasoftware.aurautilitiesplus.util;

import lombok.experimental.UtilityClass;
import tech.aurasoftware.aurautilitiesplus.AuraUtilitiesPlus;
import tech.aurasoftware.aurautilitiesplus.command.parameter.Parameter;

@UtilityClass
public class Util {


    public <T> T getParameter(Class<T> clazz, String input){

        Parameter<T> parameter = (Parameter<T>) AuraUtilitiesPlus.getInstance().getParameterManager().get(clazz);

        if(parameter == null){
            return null;
        }

        return parameter.parse(input);

    }

}
