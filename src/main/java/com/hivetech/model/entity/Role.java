package com.hivetech.model.entity;

import com.hivetech.utils.enumerates.RoleType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private RoleType name;
}
