package tech.aurasoftware.aurautilitiesplus.command.parameter;


import tech.aurasoftware.aurautilitiesplus.util.Manager;

public class ParameterManager extends Manager<Class<?>, Parameter<?>> {
    @Override
    public Class<?> getKey(Parameter<?> obj) {
        return obj.getClazz();
    }


}
