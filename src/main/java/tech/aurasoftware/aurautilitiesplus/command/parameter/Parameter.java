package tech.aurasoftware.aurautilitiesplus.command.parameter;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class Parameter<T> {

    private Class<T> clazz;
    public Parameter(Class<T> clazz){
        this.clazz = clazz;
    }

    public abstract T parse(String input);

    public boolean isParsable(String string){
        if(string == null){
            return false;
        }
        return parse(string) != null;
    }

    public abstract List<String> tabComplete();

//    public boolean handleNotParsable(LibCommandSender libCommandSender, LibCommandFrame libCommandFrame, String arg){
//        if(arg == null){
//            CoreMessages.INVALID_USAGE.send(libCommandSender, new Placeholder("{usage}", libCommandFrame.getUsage()));
//            return false;
//        }
//        return true;
//    }

}
