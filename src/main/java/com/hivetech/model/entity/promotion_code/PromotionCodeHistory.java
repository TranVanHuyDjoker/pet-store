package com.hivetech.model.entity.promotion_code;

import com.hivetech.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "promotion_code_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCodeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @OneToOne()
    @JoinColumn(name = "promotion_code_id")
    private PromotionCode promotionCode;
    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
