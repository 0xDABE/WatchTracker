import java.io.Serial;
import java.io.Serializable;

public class MediaFile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String name;
    public int duration;
    public int spent = 0;
    public boolean watched = false;

    public MediaFile(String name, int duration){
        this.name = name;
        this.duration = duration;
    }

}
