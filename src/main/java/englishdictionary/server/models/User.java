package englishdictionary.server.models;

import com.google.cloud.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String email;
    private String fullName;
    private Integer gender;
    private Integer level;
    private Integer occupation;
    private String password;
    private String lastLog;
}
