package sit.int221.itbkkbackend.v2.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@ToString
@Table(name = "taskV2", schema = "itb-kk")
public class TaskV2 {
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
    private StatusV2 status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "boardId" , referencedColumnName = "boardId",nullable = false)
    private BoardV2 board;

    @Column(name = "boardId",insertable = false,updatable = false)
    private Integer boardId;

    @Column(insertable=false, updatable=false)
    private Integer statusId;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "createdOn", insertable = false ,updatable = false)
    private ZonedDateTime createdOn;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "updatedOn", insertable = false ,updatable = false)
    private ZonedDateTime updatedOn;


    public Integer getStatusId() {
        return status == null ? null : status.getId();
    }
}
