package sit.int221.itbkkbackend.v1.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@Table(name = "task_v1", schema = "itb-kk")
public class TaskV1 {
    @Id
    @Column(name = "task_id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "task_title")
    private String title;

    @Column(name = "task_description")
    private String description;
    
    @Column(name = "task_assignees")
    private String assignees;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "task_status")
    private StatusV1 status;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonIgnore
    @Column(name = "created_on", insertable = false ,updatable = false)
    private ZonedDateTime createdOn;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonIgnore
    @Column(name = "updated_on", insertable = false ,updatable = false)
    private ZonedDateTime updatedOn;

}
