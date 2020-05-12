package OtherClasses;

public class Reponses {
    boolean operationBienEffectue;
    String message;

    public Reponses(boolean operationBienEffectue) {
        this.operationBienEffectue = operationBienEffectue;
        this.message="";
    }

    public Reponses(boolean operationBienEffectue, String message) {
        this.operationBienEffectue = operationBienEffectue;
        this.message=message;
    }
}
