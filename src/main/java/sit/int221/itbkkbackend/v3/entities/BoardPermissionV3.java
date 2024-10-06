package sit.int221.itbkkbackend.v3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;

@Setter
@Getter
@Entity
@ToString
@Table(name = "user_boards_v3",schema = "itb-kk")
public class BoardPermissionV3 {
    @EmbeddedId
    private BoardUserKey boardUserKey;

    @Column(name = "access_right")
    private String accessRight;

    @ManyToOne
    @JoinColumn(name = "user_oid",insertable = false,updatable = false)
    private UserV3 user;

    @Getter
    @Setter
    @ToString
    @Embeddable
    public static class BoardUserKey implements Serializable {
        @Column(name = "board_id")
        private String boardId;

        @Column(name = "user_oid")
        private String oid;


    }

}

