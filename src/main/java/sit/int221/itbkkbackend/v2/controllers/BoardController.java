package sit.int221.itbkkbackend.v2.controllers;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v2.dtos.BoardDTO;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
import sit.int221.itbkkbackend.v2.repositories.BoardRepositoryV2;
import sit.int221.itbkkbackend.v2.services.BoardServiceV2;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/v2/boards")
public class BoardController {
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
    public BoardDTO updateBoardMaximumTasks(@PathVariable Integer id, @RequestBody Map<String, Optional<String>> board){
        return service.updateBoardById(id,board);
    }
}
