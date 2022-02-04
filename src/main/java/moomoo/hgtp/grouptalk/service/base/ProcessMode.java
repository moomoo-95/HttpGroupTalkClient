package moomoo.hgtp.grouptalk.service.base;

public enum ProcessMode {
    DOWN(-1), SERVER(0), CLIENT(1), PROXY(2);

    private final int value;

    ProcessMode(int value){
        this.value = value;
    }

    public final int getValue(){
        return this.value;
    }
}
