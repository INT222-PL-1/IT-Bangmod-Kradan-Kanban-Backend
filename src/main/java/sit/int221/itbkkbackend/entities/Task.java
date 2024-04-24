package sit.int221.itbkkbackend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "tasks", schema = "itb-kk")
public class Task {
    @Id
    @Column(name = "taskId",nullable = false)
    private Integer id;
    
    @Column(name = "taskTitle")
    private String title;

    @Column(name = "taskDescription")
    private String description;
    
    @Column(name = "taskAssignees",nullable = false)
    private String assignees;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "taskStatus",nullable = false)
    private Status status;

    @JsonFormat(pattern =  "dd-MM-yyyy'T'HH:mm:ss.SSSXXX")
    @Column(name = "createdOn",nullable = false)
    private Timestamp createdOn;

    @JsonFormat(pattern =  "dd-MM-yyyy'T'HH:mm:ss.SSSXXX")
    @Column(name = "updatedOn",nullable = false)
    private Timestamp updatedOn;

}
