import java.io.Serializable;
import java.sql.Timestamp;

public class Packet implements Serializable {
    int type, code, checksum, identifier, sequence_no;
    Timestamp payload;

    public Packet(int type, int identifier, int sequence_no) {
        this.type = type;
        this.code = 0;
        this.checksum = 1;
        this.identifier = identifier;
        this.sequence_no = sequence_no;
        this.payload = new Timestamp(System.currentTimeMillis());
    }
}
