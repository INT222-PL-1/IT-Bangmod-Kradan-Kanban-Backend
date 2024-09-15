package sit.int221.itbkkbackend.v3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "boardV3", schema = "itb-kk")
public class BoardV3 {
    @Id
    @Column(name = "boardId")
    private String id;
    @Column(name = "owner_oid")
    private String ownerOid;
    @Column(name = "name")
    private String name;
    @Column(name = "is_limit_tasks", insertable = false)
    private Boolean isLimitTasks;
    @Column(name = "task_limit_per_status", insertable = false)
    private Integer taskLimitPerStatus;
    @Column(name = "default_statuses_config", insertable = false)
    private String defaultStatusesConfig;
    @OneToMany
    @JoinColumn(name = "boardId", referencedColumnName = "boardId")
    private List<TaskV3> tasks;
}
