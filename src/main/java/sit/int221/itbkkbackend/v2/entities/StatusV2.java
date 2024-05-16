package sit.int221.itbkkbackend.v2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

@Getter
@Setter
@Entity
@DynamicInsert
@ToString
@Table(name = "status", schema = "itb-kk")
public class StatusV2 {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "statusId")
    private Integer id;

    @Column(name = "statusName")
    private String name;

    @Column(name = "statusDescription")
    private String description;

    @Column(name = "statusColor",nullable = false)
    private String color;

    @Column(name = "is_fixed_status",insertable = false,updatable = false)
    private Boolean is_fixed_status;

    @Column(name = "is_limited_status",insertable = false,nullable = false)
    private Boolean is_limited_status;

    @Column(name = "maximum_limit",insertable = false,nullable = false)
    private Integer maximum_limit;
    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<TaskV2> tasks;
}
