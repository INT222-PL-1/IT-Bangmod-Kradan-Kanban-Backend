package sit.int221.itbkkbackend.v3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "board_v3", schema = "itb-kk")
public class BoardV3 {
    @Id
    @Column(name = "board_id")
    private String id;
    @Column(name = "owner_oid")
    private String ownerOid;
    @Column(name = "board_name")
    private String name;
    @Column(name = "is_task_limit_enabled", insertable = false)
    private Boolean isTaskLimitEnabled;
    @Column(name = "task_limit_per_status", insertable = false)
    private Integer taskLimitPerStatus;
    @Column(name = "default_status_config", insertable = false)
    private String defaultStatusConfig;
    @Column(name = "board_visibility")
    private String visibility;
    @OneToMany
    @JoinColumn(name = "board_id", referencedColumnName = "board_id")
    private List<TaskV3> tasks;
}
