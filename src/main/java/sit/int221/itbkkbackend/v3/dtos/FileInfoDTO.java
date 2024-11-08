package sit.int221.itbkkbackend.v3.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.v3.entities.FileV3;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDTO {
    private String name;
    private String type;
    private Long size;

    public FileInfoDTO(FileV3 file){
        this.name = file.getFileKey().getName();
        this.type = file.getType();
        this.size = file.getSize();

    }
}
