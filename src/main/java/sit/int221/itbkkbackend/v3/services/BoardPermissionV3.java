package sit.int221.itbkkbackend.v3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int221.itbkkbackend.v3.repositories.BoardPermissionRepositoryV3;


@Service
public class BoardPermissionV3 {
    @Autowired
    BoardPermissionRepositoryV3 boardPermissionRepository;
}
