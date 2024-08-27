package org.development.blogApi.user.entity;


// TODO change names of table to single form "account"
//@Entity
//@Table(name = "accounts")
//public class Account {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id")
//    private UUID id;
//
//    @Column(name = "login")
//    private String login;
//
//    @Column(name = "email")
//    private String email;
//
//    @Column(name = "password_hash")
//    private String passwordHash;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @OneToOne(mappedBy = "userEntity")
//    private UserEntity user;
//
//    // Constructor
//    public Account(String login, String email, String passwordHash, LocalDateTime createdAt) {
//        this.login = login;
//        this.email = email;
//        this.passwordHash = passwordHash;
//        this.createdAt = createdAt;
//    }
//
//    public Account() {}
//
//    // Getters and Setters
//    public String getLogin() {
//        return login;
//    }
//
//    public void setLogin(String login) {
//        this.login = login;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPasswordHash() {
//        return passwordHash;
//    }
//
//    public void setPasswordHash(String passwordHash) {
//        this.passwordHash = passwordHash;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    // Static factory method
//    public static Account createInstance(String login, String email, String passwordHash, LocalDateTime createdAt) {
//        return new Account(login, email, passwordHash, createdAt);
//    }
//}

