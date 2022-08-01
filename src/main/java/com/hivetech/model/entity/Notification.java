package com.hivetech.model.entity;

import com.hivetech.utils.enumerates.GuaranteeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.lang.reflect.Member;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private boolean read;
    private boolean trash;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime createAt;
}
