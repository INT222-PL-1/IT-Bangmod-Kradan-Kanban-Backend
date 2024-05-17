package sit.int221.itbkkbackend.v2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Table(name = "board",schema = "itb-kk")
@Entity
public class BoardV2 {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "boardId")
    private Integer id;

    @Column(name = "is_limit_tasks")
    private Boolean isLimitTasks;

    @Column(name = "task_limit_per_status")
    private Integer taskLimitPerStatus;

    @OneToMany
    @JoinColumn(name = "boardId",referencedColumnName = "boardId")
    private List<TaskV2> tasks;


}
