package Enum;

/**
 * Created by Brandon on 06/05/2017.
 */
public enum PacketType {
        RRQ("RRQ", 1),
        WRQ("WRQ", 2),
        DATA("DATA", 3),
        ACK("ACK", 4),
        ERROR("ERROR", 5);

        public final String tag;
        public final int code;

        PacketType(String type, int code)
        {
            this.tag = type;
            this.code = code;
        }

}
