package matt.sb.example.entities.primary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
@EqualsAndHashCode
public class UserRecord implements Serializable {
    private static final long serialVersionUID = -8789139921311825529L;
    @Id
    @Column(name="id")
    private Integer userId;

    @Column(name="firstname")
    private String firstName;

    @Column(name="lastname")
    private String lastName;
}
