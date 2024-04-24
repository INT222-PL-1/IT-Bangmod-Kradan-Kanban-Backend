package sit.int221.itbkkbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class ListMapper {

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass,ModelMapper mapper) {
        return source.stream()
                .map(entity -> mapper.map(entity, targetClass))
                .collect(Collectors.toList());
    }

}
