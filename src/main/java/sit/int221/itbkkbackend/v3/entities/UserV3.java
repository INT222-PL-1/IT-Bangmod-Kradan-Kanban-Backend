package sit.int221.itbkkbackend.v3.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@Table(name = "user_v3", schema = "itb-kk")
public class UserV3 {
    @Id
    @Column(name = "oid")
    private String oid;
    @Column(name = "name")
    private String name;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "created_on", insertable = false ,updatable = false)
    private ZonedDateTime createdOn;
    @JsonFormat(pattern =  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "updated_on", insertable = false ,updatable = false)
    private ZonedDateTime updatedOn;

}
