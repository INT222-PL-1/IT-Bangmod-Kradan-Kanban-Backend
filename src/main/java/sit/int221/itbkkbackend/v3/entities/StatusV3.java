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
@Table(name = "status_v3", schema = "itb-kk")
public class StatusV3 {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "status_id")
    private Integer id;

    @Column(name = "status_name")
    private String name;

    @Column(name = "status_description")
    private String description;

    @Column(name = "status_color", nullable = false)
    private String color;

    @Column(name = "is_predefined", insertable = false, updatable = false)
    private Boolean isPredefined;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<TaskV3> tasks;

    @Column(name = "board_id", updatable = false)
    private String boardId;
}
