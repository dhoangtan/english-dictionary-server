package englishdictionary.server.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String email;
    private String full_Name;
    private Integer gender;
    private Integer level;
    private Integer occupation;
    private String id;
}
