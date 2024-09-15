package sit.int221.itbkkbackend.v3.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

@Getter
@Setter
@Entity
@DynamicInsert
@Table(name = "statusV3", schema = "itb-kk")
public class StatusV3 {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "statusId")
    private Integer id;

    @Column(name = "statusName")
    private String name;

    @Column(name = "statusDescription")
    private String description;

    @Column(name = "statusColor", nullable = false)
    private String color;

    @Column(name = "is_fixed_status", insertable = false, updatable = false)
    private Boolean is_fixed_status;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<TaskV3> tasks;

    @Column(name = "boardId", updatable = false)
    private String boardId;
}
