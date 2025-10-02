package com.coffee.repository;


import com.coffee.constant.OrderStatus;
import com.coffee.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 쿼리 메소드를 사용하여 특정 회원의 주문 날짜가 최신인 것 부터 조회합니다.
    //cf. 좀더 복잡한 쿼리를 사용하시려면 @Query 또는 querydsl을 사용하세요.
    List<Order> findByMemberIdOrderByIdDesc(Long memberId);

    // 주문번호(id) 기준으로 모든 주문 내역을 역순 (내림차순)으로 조회하려면 JPA 메서그를 이렇게 작성하시면 됩니다.
     List<Order> findAllByOrderByIdDesc(); //이건 관리자가 사용합니다.

    // 특정 주문에 대하여 주문의 상태를 'COMPLETED'로 변경합니다.
    // 쿼리 메소드 대신 @Query 어노테이션 사용 예시 : sql 대신 JPQL
    // 주의사항
    // 1. 테이블 이름 대신 Entity 이름을 명시
    // 2. 대소문자 구분 합니다.
    @Modifying // 이 쿼리는 select 구문이 아니고, 데이터 변경을 위한 쿼리입니다.
    @Transactional // import jakarta.transaction.Transactional;
    @Query("update Order o set o.status = :status where o.id= :orderId")
    int updateOrderStatus(@Param("orderId")Long orderId,@Param("status") OrderStatus status);
}
