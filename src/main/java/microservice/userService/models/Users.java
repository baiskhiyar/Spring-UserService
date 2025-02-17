package microservice.userService.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name="username", unique = true)
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="mobile_no")
    private String mobileNo;

    @Column(name="active")
    private Boolean active;

    @Column(name="password")
    private String password;

    @Column(name="user_type")
    private String userType;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        active = true;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
//
//    @Override
//    public void setPassword(String password){
////       TODO : Need to add encryption
//        this.password = "askljashduadhsfjkaldhf" + password + "alkjsdfpqowierojof";
//    }
}
