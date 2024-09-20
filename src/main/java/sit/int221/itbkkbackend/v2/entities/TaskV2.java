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
@Table(name = "task_v2", schema = "itb-kk")
public class TaskV2 {
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

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id", nullable = false)
    private StatusV2 status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "board_id" , referencedColumnName = "board_id",nullable = false)
    private BoardV2 board;

    @Column(name = "board_id",insertable = false,updatable = false)
    private Integer boardId;

    @Column(insertable=false, updatable=false)
    private Integer statusId;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "created_on", insertable = false ,updatable = false)
    private ZonedDateTime createdOn;

    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "updated_on", insertable = false ,updatable = false)
    private ZonedDateTime updatedOn;


    public Integer getStatusId() {
        return status == null ? null : status.getId();
    }
}
