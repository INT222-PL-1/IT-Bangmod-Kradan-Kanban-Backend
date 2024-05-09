package sit.int221.itbkkbackend.v2.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.StatusV2;
import sit.int221.itbkkbackend.v2.services.StatusServiceV1;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:4173",
        "http://ip23pl1.sit.kmutt.ac.th:5173",
        "http://ip23pl1.sit.kmutt.ac.th:3000",
        "http://ip23pl1.sit.kmutt.ac.th:4173",
        "http://ip23pl1.sit.kmutt.ac.th"
})

@RestController
@RequestMapping("/v2/statuses")
public class StatusController {

    @Autowired
    private StatusServiceV1 service;

    @GetMapping("")
    public ResponseEntity<Object> getAllStatus(@RequestParam(defaultValue = "false") Boolean count){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(service.findAllStatus(count)) ;
    }

    @PostMapping("")
    public StatusV2 addStatus(@Valid @RequestBody StatusDTO status){
        return service.addStatus(status);
    }

    @PutMapping("/{id}")
    public StatusV2 updateStatus(@PathVariable Integer id , @Valid @RequestBody StatusDTO status){
        return service.editStatus(id,status);
    }

    @DeleteMapping("/{id}")
    public StatusV2 deleteStatus(@PathVariable Integer id){
        return service.deleteStatus(id);
    }

    @DeleteMapping("/{oldId}/{newId}")
    public StatusV2 transferAndDeleteStatus(@PathVariable Integer oldId, @PathVariable Integer newId){
        return service.transferAndDeleteStatus(oldId,newId);
    }
}
