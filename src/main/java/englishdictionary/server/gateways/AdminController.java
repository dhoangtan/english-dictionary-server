package englishdictionary.server.gateways;

import englishdictionary.server.services.AdminService;
import englishdictionary.server.utils.ControllerUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/admin/")
public class AdminController {
    @Autowired
    private ControllerUtilities utilFuncs;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private String getFunctionCall(String functionName, String resource) {
        return "[AdminController] call [" + functionName + "] to resource " + resource;
    }

    @Autowired
    private AdminService adminService;

    @PostMapping("edit/gender/{docId}")
    public ResponseEntity<String> editGender(@RequestBody String content, @PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("editGender", resource);
        try{
            logger.info(prompt);
            adminService.editGender(content, docId);
            return ResponseEntity.ok().build();
        }catch (ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Interrupted Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("edit/level/{docId}")
    public ResponseEntity<String> editLevel(@RequestBody String content, @PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("editLevel", resource);
        try{
            logger.info(prompt);
            adminService.editLevel(content, docId);
            return ResponseEntity.ok().build();
        }catch (ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Interrupted Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("edit/occupation/{docId}")
    public ResponseEntity<String> editOccupation(@RequestBody String content, @PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("editOccupation", resource);
        try{
            logger.info(prompt);
            adminService.editOccupation(content, docId);
            return ResponseEntity.ok().build();
        }catch (ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Interrupted Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("delete/gender/{docId}")
    public ResponseEntity<String> deleteGender(@PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteGender", resource);
        try{
            logger.info(prompt);
            adminService.deleteGender(docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.error("An error occurred when getting resource " + resource + " - Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("delete/level/{docId}")
    public ResponseEntity<String> deleteLevel(@PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteLevel", resource);
        try{
            logger.info(prompt);
            adminService.deleteLevel(docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.error("An error occurred when getting resource " + resource + " - Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("delete/occupation/{docId}")
    public ResponseEntity<String> deleteOccupation(@PathVariable("docId") String docId, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteOccupation", resource);
        try{
            logger.info(prompt);
            adminService.deleteOccupation(docId);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            logger.error("An error occurred when getting resource " + resource + " - Error - \n Error message:\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("delete/user/{id}")
    public ResponseEntity<String> setUserActiveState(@PathVariable("id") String id, HttpServletRequest request){
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("deleteUser", resource);
        String errorMessage = "The document of user with id ["+id+"] could not be found";
        try{
            logger.info(prompt);
            adminService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (ExecutionException  e) {
            logger.error("An error occurred when executing " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No document to execute");
        } catch (InterruptedException e) {
            logger.error("InterruptedException");
            throw new RuntimeException(e);
        }
    }
}
