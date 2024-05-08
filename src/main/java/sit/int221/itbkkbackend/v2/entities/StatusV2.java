package sit.int221.itbkkbackend.v2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "status", schema = "kanban")
public class StatusV2 {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "statusId")
    private Integer id;

    @Column(name = "statusName")
    private String name;

    @Column(name = "statusDescription")
    private String description;

    @Column(name = "statusColor")
    private String color;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<TaskV2> tasks;
}
