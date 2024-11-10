package sit.int221.itbkkbackend.utils;

import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.itbkkbackend.v3.dtos.FileInfoDTO;
import sit.int221.itbkkbackend.v3.entities.FileV3;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListMapper {

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass,ModelMapper mapper) {
        return source.stream()
                .map(entity -> mapper.map(entity, targetClass))
                .collect(Collectors.toList());
    }

    public static List<FileV3> mapMultipartFilesToFileList(MultipartFile[] files) {
        return Arrays.stream(files)
                .map(FileV3::new) // Convert each MultipartFile to a FileV3 instance
                .collect(Collectors.toList());
    }
    public static List<FileInfoDTO> mapFileListToFileInfoDTOList(List<FileV3> files, Integer taskId, String boardId) {
        return files.stream()
                .map(file -> new FileInfoDTO(file, taskId, boardId)) // Pass taskId and boardId explicitly
                .collect(Collectors.toList());
    }

}
