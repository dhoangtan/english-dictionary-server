package englishdictionary.server.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUploadAvatarDto {
    private MultipartFile file;
    private String userId;
}
