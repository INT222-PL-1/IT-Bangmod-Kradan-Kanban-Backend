package sit.int221.itbkkbackend.v1.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sit.int221.itbkkbackend.v1.entities.Status;

import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@Table(name = "tasks", schema = "itb-kk")
public class Task {
    @Id
    @Column(name = "taskId",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "taskTitle")
    private String title;

    @Column(name = "taskDescription")
    private String description;
    
    @Column(name = "taskAssignees")
    private String assignees;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "taskStatus")
    private Status status;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonIgnore
    @Column(name = "createdOn", insertable = false ,updatable = false)
    private ZonedDateTime createdOn;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonIgnore
    @Column(name = "updatedOn", insertable = false ,updatable = false)
    private ZonedDateTime updatedOn;

}
