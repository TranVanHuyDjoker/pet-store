package com.hivetech.repository;


import com.hivetech.model.dto.ReportOrderForMonth;
import com.hivetech.model.dto.ReportOrderForQuarter;
import com.hivetech.model.dto.ReportOrderForWeek;
import com.hivetech.model.dto.ReportOrderForYear;
import com.hivetech.model.entity.Order;
import com.hivetech.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, String> {
    Optional<Order> findByVerifyCode(String code);

    @Query(value = "SELECT o FROM Order o " +
            " WHERE o.id LIKE CONCAT('%',:keyword,'%')")
    Page<Order> paging(@Param("keyword") String keyword, Pageable pageable);

    List<Order> findByUser(User user);

    @Query(value = "SELECT COUNT(o.id) as quantity ,date_part('year', o.create_at) as year , date_part('month',o.create_at) as month " +
            "FROM corder o " +
            "WHERE o.create_at > (current_date - INTERVAL '12 months')  and o.is_paid = true " +
            "GROUP BY year,month",
            nativeQuery = true)
    List<ReportOrderForMonth> reportOrderSuccessOfMonth();

    @Query(value = "SELECT COUNT(o.id) as quantity ,date_part('year', o.create_at) as year " +
            "FROM corder o " +
            "WHERE o.is_paid = true " +
            "GROUP BY year",
            nativeQuery = true)
    List<ReportOrderForYear> reportOrderSuccessOfYear();

    @Query(value = "SELECT COUNT(o.id) as quantity ,date_part('quarter', o.create_at) as quarter, date_part('year', o.create_at) as year " +
            "FROM corder o " +
            "WHERE o.create_at > (current_date - INTERVAL '12 months')  and o.is_paid = true " +
            "GROUP BY quarter, year",
            nativeQuery = true)
    List<ReportOrderForQuarter> reportOrderSuccessOfQuarter();

    @Query(value = "SELECT COUNT(o.id) as quantity, CAST (o.create_at as DATE ) as date1, date_part('day',o.create_at) as date, date_part('month',o.create_at) as month " +
            "FROM corder o " +
            "WHERE o.is_paid = true and o.create_at between  date_trunc('week',current_date) and date_trunc('week',current_date) + '6 days'" +
            "GROUP BY date1, date, month " +
            "ORDER BY date1 ASC"
            , nativeQuery = true)
    List<ReportOrderForWeek> reportOrderSuccessForWeek();

}
