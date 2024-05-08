package sit.int221.itbkkbackend.v2.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@ToString
@Table(name = "tasksV2", schema = "kanban")
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

    @ManyToOne
    @JoinColumn(name = "statusId", referencedColumnName = "statusId", nullable = false)
    private Status status;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "createdOn", insertable = false ,updatable = false)
    private ZonedDateTime createdOn;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "updatedOn", insertable = false ,updatable = false)
    private ZonedDateTime updatedOn;
}
