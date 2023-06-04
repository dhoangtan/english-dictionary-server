package englishdictionary.server.gateways;

import englishdictionary.server.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/admin/")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @PostMapping("edit/gender/{docId}")
    public ResponseEntity<String> editGender(@RequestBody String content, @PathVariable("docId") String docId) throws ExecutionException, InterruptedException {
        try{
            adminService.editGender(content, docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("delete/gender/{docId}")
    public ResponseEntity<String> deleteGender(@PathVariable("docId") String docId){
        try{
            adminService.deleteGender(docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("delete/user/{id}")
    public ResponseEntity<String> setUserActiveState(@PathVariable("id") String id){
        try{
            adminService.deleteUser(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
