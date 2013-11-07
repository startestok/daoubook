package com.daou.book.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE LOWER(u.lastName) = LOWER(?1)")
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "creation_time", nullable = false)
    private Date creationTime;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "modification_time", nullable = false)
    private Date modificationTime;
    
    @Version
    private long version = 0;

    public Long getId() {
        return id;
    }

    public static Builder getBuilder(String firstName, String lastName) {
        return new Builder(firstName, lastName);
    }
    
    public Date getCreationTime() {
        return creationTime;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Transient
    public String getName() {
        StringBuilder name = new StringBuilder();
        
        name.append(firstName);
        name.append(" ");
        name.append(lastName);
        
        return name.toString();
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public long getVersion() {
        return version;
    }

    public void update(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    @PreUpdate
    public void preUpdate() {
        modificationTime = new Date();
    }
    
    @PrePersist
    public void prePersist() {
        Date now = new Date();
        creationTime = now;
        modificationTime = now;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class Builder {
        User built;

        Builder(String firstName, String lastName) {
            built = new User();
            built.firstName = firstName;
            built.lastName = lastName;
        }

        public User build() {
            return built;
        }
    }

    protected void setId(Long id) {
        this.id = id;
    }
}
