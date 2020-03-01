package ChatForm;

public class CloseClass {
    boolean closeBit=false;
    Object oClose;
    String ProcClose;
    public CloseClass ()
    {
        System.out.println("CreateClose class");
    }

    public void SetCloseEvent(Object OClose, String ProcClose){
        this.oClose = OClose;
        this.ProcClose = ProcClose;
    }
    public void Close()
    {
        closeBit=true;
        System.out.println("Closing..........");
    }

}
