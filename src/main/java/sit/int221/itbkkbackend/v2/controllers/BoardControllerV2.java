package sit.int221.itbkkbackend.v2.controllers;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v2.dtos.BoardDTO;
import sit.int221.itbkkbackend.v2.services.BoardServiceV2;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:4173",
        "http://ip23pl1.sit.kmutt.ac.th:5173",
        "http://ip23pl1.sit.kmutt.ac.th:3000",
        "http://ip23pl1.sit.kmutt.ac.th:4173",
        "http://ip23pl1.sit.kmutt.ac.th",
        "http://intproj23.sit.kmutt.ac.th",
        "https://ip23pl1.sit.kmutt.ac.th:5173",
        "https://ip23pl1.sit.kmutt.ac.th:3000",
        "https://ip23pl1.sit.kmutt.ac.th:4173",
        "https://ip23pl1.sit.kmutt.ac.th",
        "https://intproj23.sit.kmutt.ac.th"
})
@RestController
@RequestMapping("/v2/boards")
public class BoardControllerV2 {
    @Autowired
    private BoardServiceV2 service;
    @Autowired
    private ModelMapper mapper;
    @GetMapping("")
    public List<BoardDTO> getAllBoards(){
        return service.findAllBoard();
    }
    @GetMapping("/{id}")
    public BoardDTO getBoard(@PathVariable Integer id){return mapper.map(service.findById(id),BoardDTO.class);}
    @PatchMapping("/{id}/maximum-task")
    public BoardDTO updateBoardMaximumTasks(@PathVariable Integer id, @RequestBody Map<String, Optional<Object>> board){
        return service.updateBoardById(id,board);
    }
}
