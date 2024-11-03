package sit.int221.itbkkbackend.v3.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardListDTO {
    private List<BoardDTO> personalBoards;
    private List<BoardDTO> collaborativeBoards;
}
