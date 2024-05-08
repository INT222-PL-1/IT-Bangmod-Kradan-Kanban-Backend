package sit.int221.itbkkbackend.v2.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.Status;
import sit.int221.itbkkbackend.v2.services.StatusService;

@CrossOrigin
@RestController
@RequestMapping("/v2/statuses")
public class StatusController {

    @Autowired
    private StatusService service;

    @GetMapping("")
    public ResponseEntity<Object> getAllStatus(@RequestParam(defaultValue = "false") Boolean count){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(service.findAllStatus(count)) ;
    }

    @PostMapping("")
    public Status addStatus(@Valid @RequestBody StatusDTO status){
        return service.addStatus(status);
    }

    @PutMapping("/{id}")
    public Status updateStatus(@PathVariable Integer id ,@Valid @RequestBody StatusDTO status){
        return service.editStatus(id,status);
    }

    @DeleteMapping("/{id}")
    public Status deleteStatus(@PathVariable Integer id){
        return service.deleteStatus(id);
    }

    @DeleteMapping("/{oldId}/{newId}")
    public Status transferAndDeleteStatus(@PathVariable Integer oldId, @PathVariable Integer newId){
        return service.transferAndDeleteStatus(oldId,newId);
    }
}
