package sit.int221.itbkkbackend.v2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Table(name = "board_v2",schema = "itb-kk")
@Entity
public class BoardV2 {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "board_id")
    private Integer id;

    @Column(name = "is_task_limit_enabled")
    private Boolean isTaskLimitEnabled;

    @Column(name = "task_limit_per_status")
    private Integer taskLimitPerStatus;

    @OneToMany
    @JoinColumn(name = "board_id",referencedColumnName = "board_id")
    private List<TaskV2> tasks;


}
