package tech.aurasoftware.aurautilitiesplus.command;

import lombok.Getter;
import lombok.Setter;
import tech.aurasoftware.aurautilitiesplus.command.annotation.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public abstract class AuraSubCommand implements AuraCommandFrame{
    private final String name;

    private String usage;

    private String permission;

    private List<String> aliases;
    private boolean requiresPlayer = false;

    private Class<?>[] parameters = new Class<?>[0];
    private boolean[] optional = new boolean[0];



    public AuraSubCommand(String name) {
        Constructor<?> constructor;
        this.name = name;
        try {
            constructor = getClass().getConstructors()[0];
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if (constructor.isAnnotationPresent(Alias.class)) {
            Alias alias = constructor.getAnnotation(Alias.class);
            this.setAliases(Arrays.asList(alias.value()));
        }
        if (constructor.isAnnotationPresent(Usage.class)) {
            Usage usage = constructor.getAnnotation(Usage.class);
            this.usage = usage.value();
        }

        if (constructor.isAnnotationPresent(Permission.class)) {
            Permission permission = constructor.getAnnotation(Permission.class);
            this.permission = permission.value();
        }

        if(constructor.isAnnotationPresent(RequiresPlayer.class)){
            this.requiresPlayer = true;
        }

        if(constructor.isAnnotationPresent(Parameters.class)){
            Parameters parameters = constructor.getAnnotation(Parameters.class);
            this.parameters = parameters.value();
            this.optional = parameters.optional();
        }
    }
}
